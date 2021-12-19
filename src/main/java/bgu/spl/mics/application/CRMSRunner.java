package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;

import bgu.spl.mics.application.services.TimeService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    //fields:
    public static int duration;
    public static int tickTime;
    private static ArrayList<Student> students=new ArrayList<>();
    private static ArrayList<GPU> gpus=new ArrayList<>();
    private static ArrayList<CPU> cpus=new ArrayList<>();
    private static ArrayList<ConfrenceInformation> conferences=new ArrayList<>();
    private static ArrayList<Thread> myThreads= new ArrayList<>();

    //running-extracting info from json file->initializing and running->results in output file.
    public static void main(String[] args) {
        //todo throw exceptions at correct places.
        //create a message bus and cluster
        MessageBusImpl msgBus=MessageBusImpl.getInstance();
        Cluster cluster=Cluster.getInstance();
        //start by creating everything with input file
        File input= new File(args[0]); //takes the first parameter-assumes to be file path
        try {
            //preparation
            JsonElement fileElement= JsonParser.parseReader(new FileReader(input));
            JsonObject fileObj=fileElement.getAsJsonObject();

            //starting to extract!

            //extract students:
            JsonArray s= fileObj.get("Students").getAsJsonArray();//gets json array of students
            for(JsonElement getElement: s){ //process the array of students
                JsonObject jsonStudent=getElement.getAsJsonObject();
                //extract data
                String name= jsonStudent.get("name").getAsString();
                String department=jsonStudent.get("department").getAsString();
                String statusAsString=jsonStudent.get("status").getAsString();
                Student.Degree degree;
                //gets the degree
                if(statusAsString.equals("PhD"))
                    degree= Student.Degree.PhD;
                else if(statusAsString.equals("MSc"))
                    degree= Student.Degree.MSc;
                else{System.out.println("bad input-no such degree for student"+name); continue;}
                LinkedList<Model> models=new LinkedList<>();
                Student currStudent=new Student(name,department,degree,models);
                students.add(currStudent);
                myThreads.add(new Thread(currStudent.getService()));

                JsonArray studentJsonModels=jsonStudent.get("models").getAsJsonArray();//array of models
                //note: we will try to avoid primitive types as we could get a null if file is not adequate.
                for(JsonElement e: studentJsonModels){
                    JsonObject currentModel=e.getAsJsonObject();
                    //get all the data about the model
                    String modelName=currentModel.get("name").getAsString();
                    String type=currentModel.get("type").getAsString();
                    Data modelData;
                    Integer size=currentModel.get("size").getAsInt();
                    if(size==null){
                        System.out.println("a model got no size");//bad input
                        continue;
                    }

                    //convert data from string to actual data (type and size):
                    if(type.equals("images"))
                        modelData=new Data(Data.Type.Images,size);
                    else if(type.equals("Text"))
                        modelData=new Data(Data.Type.Text,size);
                    else if(type.equals("Tabular"))
                        modelData=new Data(Data.Type.Tabular,size);
                    else{//bad input.
                        System.out.println("bad input- no such model type at student "+name+" model "+modelName);
                        continue;}
                    //adds the model to the student for training.
                    models.add(new Model(modelName, modelData,currStudent));
                }
            }

            //extract GPUS
            JsonArray jasonGpus=fileObj.get("GPUS").getAsJsonArray();
            int i=1;
            for(JsonElement e:jasonGpus){
                String jsonType=e.getAsString();
                GPU.Type gpuType;
                if(jsonType.equals("RTX3090"))
                    gpuType= GPU.Type.RTX3090;
                else if(jsonType.equals("RTX2080"))
                    gpuType= GPU.Type.RTX2080;
                else if(jsonType.equals("GTX1080"))
                    gpuType= GPU.Type.GTX1080;
                else{//bad input
                    System.out.println("bad input- no such gpu- try to fix gpu at "+gpus.size()+
                            " to: RTX3090,RTX2080,GTX1080");
                    continue;
                }
                GPU cur=new GPU(gpuType,"gpu"+i);
                gpus.add(cur);
                myThreads.add(new Thread(cur.getGPUService()));
                i++;//for gpu name;
            }

            //extract CPUS
            i=1;
            JsonArray jsonCpus=fileObj.get("CPUS").getAsJsonArray();
            for(JsonElement e: jsonCpus){
                Integer cores=e.getAsInt();
                if(cores==null){ //bad input
                    System.out.println("bad input-no cores specified for cpu "+i);
                    continue;
                }
                CPU cur=new CPU(cores,"cpu"+i);
                cpus.add(cur);
                myThreads.add(new Thread(cur.getService()));
                i++;
            }

            //extract conferences
            JsonArray jsonConferences=fileObj.get("Conferences").getAsJsonArray();
            for(JsonElement e: jsonConferences) {
                JsonObject conf=e.getAsJsonObject();
                String name=conf.get("name").getAsString();
                Integer date=conf.get("date").getAsInt();
                if(date==null){ //bad input
                    System.out.println("bad input-no date specified for conference "+name);
                    continue;
                }
                ConfrenceInformation info=new ConfrenceInformation(name,date);
                conferences.add(info);
                myThreads.add(new Thread(info.getService()));
            }
            //extract tick time
            Integer tt=fileObj.get("TickTime").getAsInt();
            if(tt==null){ //bad input
                System.out.println("bad input-not tick time specified");
                return;
            }
            tickTime=tt;
            //extract duration
            Integer durat=fileObj.get("Duration").getAsInt();
            if(durat==null){ //bad input
                System.out.println("bad input-no duration time specified");
                return;
            }
            duration=durat;
            TimeService t=new TimeService(tickTime,duration);
            myThreads.add(new Thread(t));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
         //start everything!
        Thread time=myThreads.remove(0);
        for(Thread m:myThreads) {
            m.start(); //start the thread for every microservice
        }

        //wait until all threads are done running before trying to get output
        for (Thread mThread : myThreads) {
            try {
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //get output file
        String output="";
        for(Student s:students)
            output=output+s.toString()+'\n';
        for (ConfrenceInformation c:conferences)
            output+=c.toString()+'\n';
        int gpu_time=cluster.getGPUtime();
        int cpu_time=cluster.getCPUtime();
        int processed_num=cluster.getNumberOfProcessedBatches();
        output+="GPU time used: "+gpu_time+'\n';
        output+="CPU time used: "+cpu_time+'\n';
        output+="CPUS processed: "+processed_num+" batches"+'\n';

        try {
            FileWriter writer=new FileWriter(args[1]);
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}