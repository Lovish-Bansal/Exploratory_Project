package PSO;

import utils.Constants;
import utils.GenerateMatrices;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.*;

/**
 * A simple example showing how to create
 * a datacenter with two hosts and run two
 * cloudlets on it. The cloudlets run in
 * VMs with different MIPS requirements.
 * The cloudlets will take different time
 * to complete the execution depending on
 * the requested VM performance.
 * 
 **/
public class PSO_Scheduler {

    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmList;
    private static Datacenter[] datacenter;
    private static PSO PSO_obj;
    private static double mapping[];
    private static double[][] commMatrix;
    private static double[][] execMatrix;

    private static List<Vm> createVM(int userId, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<Vm> list = new LinkedList<Vm>();

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 250;
        long bw = 1000;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        Vm[] vm = new Vm[vms];

        for (int i = 0; i < vms; i++) {
            vm[i] = new Vm(datacenter[i].getId(), userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }

        return list;
    }

    private static List<Cloudlet> createCloudlet(int userId, int cloudlets, int idShift) {
        LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

        //cloudlet parameters
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet[] cloudlet = new Cloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            int dcId = (int) (mapping[i]);
            long length = (long) (1e3 * (commMatrix[i][dcId] + execMatrix[i][dcId]));
            cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet[i].setUserId(userId);
            list.add(cloudlet[i]);
        }

        return list;
    }

    public static void main(String[] args) {
        Log.printLine("Starting PSO Scheduler...");
		new GenerateMatrices();

        commMatrix = GenerateMatrices.getCommMatrix();
        execMatrix = GenerateMatrices.getExecMatrix();
        PSO_obj = new PSO();
        mapping = PSO_obj.run();

        try {
            int num_user = 1;   // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events

            CloudSim.init(num_user, calendar, trace_flag);

            // Second step: Create Datacenters
            
            List<Host> hostList = new ArrayList<Host>();
       	 List<Pe> peList = new ArrayList<Pe>();
       	 int mips = 1000;
       	 peList.add(new Pe(0, new PeProvisionerSimple(mips)));
       	 int hostId = 0;
            int ram = 2048; //host memory (MB)
            long storage = 1000000; //host storage
            int bw = 10000;
            hostList.add(
                    new Host(
                            hostId,
                            new RamProvisionerSimple(ram),
                            new BwProvisionerSimple(bw),
                            storage,
                            peList,
                            new VmSchedulerTimeShared(peList)
                    )
            );
            
            String arch = "x86";      // system architecture
            String os = "Linux";          // operating system
            String vmm = "Xen";
            double time_zone = 10.0;         // time zone this resource located
            double cost = 3.0;              // the cost of using processing in this resource
            double costPerMem = 0.05;        // the cost of using memory in this resource
            double costPerStorage = 0.1;    // the cost of using storage in this resource
            double costPerBw = 0.1;            // the cost of using bw in this resource
            LinkedList<Storage> storageList = new LinkedList<Storage>();
            
            DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                    arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);
            
            datacenter = new Datacenter[Constants.data_count];
            for (int i = 0; i < Constants.data_count; i++) {
                 
                 datacenter[i]=null;
                 try {
                     datacenter[i] = new Datacenter("Datacenter_" + i, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
         }

            //Third step: Create Broker
            PSODatacenterBroker broker = new PSODatacenterBroker("Broker_0");
            int brokerId = broker.getId();

            //Fourth step: Create VMs and Cloudlets and send them to broker
            vmList = createVM(brokerId, Constants.data_count);
            cloudletList = createCloudlet(brokerId, Constants.task_count, 0);

            
            HashSet<Integer> dcIds = new HashSet<>();
            HashMap<Integer, Integer> hm = new HashMap<>();
            for (Datacenter dc : datacenter) {
                if (!dcIds.contains(dc.getId()))
                    dcIds.add(dc.getId());
            }
            Iterator<Integer> it = dcIds.iterator();
            for (int i = 0; i < mapping.length; i++) {
                if (!hm.containsKey((int) mapping[i]))  hm.put((int) mapping[i], it.next());
            }
            for (int i = 0; i < mapping.length; i++)
                mapping[i] = hm.containsKey((int) mapping[i]) ? hm.get((int) mapping[i]) : mapping[i];
			
            broker.submitVmList(vmList);
            broker.setMapping(mapping);
            broker.submitCloudletList(cloudletList);


            // Fifth step: Starts the simulation
            CloudSim.startSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();
			
            printCloudletList(newList);

            Log.printLine(PSO_Scheduler.class.getName() + " finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Cloudsim has been terminated due to an unexpected error");
        }
    }

  
    
    private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time" + indent
				+ "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		double mx = 0.0;
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));
				
				mx=Math.max(mx, cloudlet.getFinishTime());
			}
		}
		
		Log.printLine("Maximum finish time:"+indent+mx);
		PSO_obj.printBestFitness();
	}
}