package process;

import process.PCB;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Process {

    private static final int ROUND = 2; //时间片
    public static ArrayList<PCB> pcb = new ArrayList<>(); //保存所有PCB
    public static ArrayList<PCB> run; //保存每个时刻的运行进程
    public static ArrayList<HashSet<PCB>> ready; //保存每个时刻就绪队列
    public static ArrayList<HashSet<PCB>> block; //保存每个时刻阻塞队列
    public static ArrayList<HashSet<PCB>> finish; //保存每个时刻完成队列
    public ArrayList<PCB> workArr;
    public int size;
    public String algorithm; //选择的算法
    
    public Process(String algorithm, int flag) {
        this.algorithm = algorithm;
        if (flag == 0) {
            init1();
        } else {
            init2();
        }
    }
    
    /*初始化*/
    private void init1() {
        ready = new ArrayList<>();
        block = new ArrayList<>();
        finish = new ArrayList<>();
        run = new ArrayList<>();
        
        Scanner sc = null;
        try {
            sc = new Scanner(new FileReader("D:\\data.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert sc != null;
        this.size = sc.nextInt();
        int i = 0;
        while (i < this.size) {
            String name = sc.next();
            double arriveTime = sc.nextDouble();
            double runTime = sc.nextDouble();
            int priority = sc.nextInt();
            
            PCB node = new PCB(name, arriveTime, runTime, priority);
            pcb.add(node);
            i ++;
        }
    }
    
    private void init2() {
        ready = new ArrayList<>();
        block = new ArrayList<>();
        finish = new ArrayList<>();
        run = new ArrayList<>();
    }
    
    /*运行*/
    public void doIt() {
        System.out.println("你选择了" + algorithm);
        switch (algorithm) {
            case "先来先服务算法" -> fcfs(pcb);
            case "短进程优先算法" -> spf(pcb);
            case "优先级调度算法（非抢占式）" -> ps(pcb);
            case "时间片轮转算法" -> rr(pcb);
        }
    }
    
    /*先来先服务*/
    public void fcfs(ArrayList<PCB> PCBArr) {
        double time;
        //根据到达时间将输入队列排序，同时到达的进程根据序号排序
        for(int i = 0; i < PCBArr.size(); i++) {
            for(int j=i+1;j<PCBArr.size(); j++) {
                if(PCBArr.get(i).arriveTime > PCBArr.get(j).arriveTime) {
                    Collections.swap(PCBArr, i, j);
                }
            }
        }
        //存储结果数组
        workArr = new ArrayList<>();
        time = buildWork(PCBArr);
        processRun(workArr, time);
        printWork(workArr);
        printQueue();
        setTurnTime();
    }

    /*短进程优先*/
    public void spf(ArrayList<PCB> PCBArr) {
        //根据到达时间获取第一个到达并且服务时间较短的进程
        PCB first;
        first = PCBArr.get(0);
        for(int i=0;i<PCBArr.size();i++) {
            if(first.arriveTime == PCBArr.get(i).arriveTime) {
                if(first.runTime > PCBArr.get(i).runTime) {
                    first = PCBArr.get(i);
                }
            } else if(first.arriveTime > PCBArr.get(i).arriveTime) {
                first = PCBArr.get(i);
            }
        }
        //复制原PCB数组
        ArrayList<PCB> tempArr = new ArrayList<>(PCBArr);
        //第一个进入工作队列中
        workArr = new ArrayList<>();  //存储结果数组
        workArr.add(first);
        workArr.get(0).startTime = first.arriveTime;
        workArr.get(0).finishTime = first.arriveTime + first.runTime;

        //删除已经进入工作队列的第一个进程的PCB
        tempArr.remove(first);
        double time = 0;
        //剩下的进程通过最短进程优先调度算法依次进入工作队列
        while(!tempArr.isEmpty()) {
            ArrayList<PCB> temp = new ArrayList<>();
            double lastFinshTime = workArr.get(workArr.size()-1).finishTime;
            //筛选出在上一个进程结束前到达的进程放入temp数组
            for(PCB p : tempArr) {
                if(p.arriveTime < lastFinshTime) {
                    temp.add(p);
                }
            }
            if(temp.isEmpty()){
                temp.addAll(tempArr);
            }
            //筛选出temp数组中最短的进程first2
            PCB first2;
            first2 = temp.get(0);
            for(int i=0;i<temp.size();i++) {
                if(first2.runTime > temp.get(i).runTime)
                    first2 = temp.get(i);
            }
            //将first2对应进程放入工作队列并运行，同时在临时复制的PCB（tempArr)中删除该进程的PCB
            first2.startTime = lastFinshTime;
            first2.finishTime = first2.startTime + first2.runTime;
            first2.waitTime = first2.startTime - first2.arriveTime;
            time = first2.finishTime;
            workArr.add(first2);
            tempArr.remove(first2);
        }
        processRun(workArr, time);
        printWork(workArr);
        printQueue();
        setTurnTime();
    }
    
    /*优先级调度*/
    public void ps(ArrayList<PCB> PCBArr) {
        //先对PCB序列通过到达时间进行升序排序
        PCBArr.sort((p1, p2) -> p1.arriveTime < p2.arriveTime ? -1 : 1);
        PCB[] pcbArr = PCBArr.toArray(new PCB[0]);
        //模拟运行时进行优先级排序（非抢占式）
        double time = pcbArr[0].arriveTime;
        for (int i = 0; i < pcbArr.length; i++) {
            int k = i;
            while (k < pcbArr.length && pcbArr[i].arriveTime == pcbArr[k].arriveTime) k++;
            Arrays.sort(pcbArr, i, k, (p1, p2) -> p1.priority < p2.priority ? 1 : -1);
            time += pcbArr[i].runTime;
            for (int j = i + 1; j < pcbArr.length; j++) {
                int l = j;
                while (l < pcbArr.length && pcbArr[l].arriveTime <= time) l++;
                Arrays.sort(pcbArr, i + 1, l, (p1, p2) -> p1.priority < p2.priority ? 1 : -1);
            }
        }
        PCBArr = new ArrayList<>(List.of(pcbArr));
        workArr = new ArrayList<>();
        time = buildWork(PCBArr);
        processRun(workArr, time);
        printWork(workArr);
        printQueue();
        setTurnTime();
    }
    
    /*时间片轮转*/
    public void rr(ArrayList<PCB> PCBArr) {
        //只用于本算法的模拟就绪队列
        Deque<PCB> readyQueue = new ArrayDeque<>();
        workArr = new ArrayList<>();
        //先将进程按照到达顺序进行升序排列
        PCBArr.sort((p1, p2) -> p1.arriveTime < p2.arriveTime ? -1 : 1);
        double time = PCBArr.get(0).arriveTime; //初始时间设置为第一个进程的到达时间
        run.add(new PCB(null, -1, -1, -1)); //第0秒时没有进程
        ready.add(new HashSet<>());
        finish.add(new HashSet<>());
        readyQueue.offer(PCBArr.get(0)); //将第一个进程放入readyQueue
        int i = 1;
        while (!readyQueue.isEmpty()) {
            boolean t = false;
            PCB head = readyQueue.peek(); //队头
            assert head != null;
            head.state = "running";
            if (head.cpuTime + ROUND >= head.runTime) {
                for (int k = 0; k < head.runTime - head.cpuTime; k++) {
                    run.add(head);
                    HashSet<PCB> readyMoment = new HashSet<>();
                    for (PCB p : PCBArr) {
                        if (Objects.equals(p.state, "ready")) {readyMoment.add(p);}
                    }
                    ready.add(readyMoment);
                    HashSet<PCB> finishMoment = new HashSet<>();
                    for (PCB p : PCBArr) {
                        if (Objects.equals(p.state, "finish")) {finishMoment.add(p);}
                    }
                    finish.add(finishMoment);
                }
                time += head.runTime - head.cpuTime;
                head.cpuTime = head.runTime;
                head.finishTime = time;
                head.waitTime = head.finishTime - head.arriveTime;
                head.state = "finish";
                t = true;
                
            } else {
                head.cpuTime += ROUND;
                time += ROUND;
                for (int k = 0; k < ROUND; k++) {
                    run.add(head);
                    HashSet<PCB> readyMoment = new HashSet<>();
                    for (PCB p : PCBArr) {
                        if (Objects.equals(p.state, "ready")) {readyMoment.add(p);}
                    }
                    ready.add(readyMoment);
                    HashSet<PCB> finishMoment = new HashSet<>();
                    for (PCB p : PCBArr) {
                        if (Objects.equals(p.state, "finish")) {finishMoment.add(p);}
                    }
                    finish.add(finishMoment);
                }
                head.state = "ready";
            }
            //运行这个时间片之后，若有进程到达时间小于等于当前时间，则插入readyQueue
            for (int j = i; j < PCBArr.size(); j++) {
                PCB p = PCBArr.get(j);
                if (!Objects.equals(p.name, head.name) && !Objects.equals(p.state, "finish") && p.arriveTime <= time) {
                    readyQueue.offer(p);
                    i++;
                }
            }
            workArr.add(readyQueue.poll());
            //若当前进程经过一个时间片之后并没有运行完，则从对头弹出，插入队尾
            if (!t) {
                readyQueue.offer(head);
            }
        }
        printWork(workArr);
        printQueue();
        setTurnTime();
    }
    
    /*在控制台输出工作序列与信息*/
    public void printWork(ArrayList<PCB> queue) {
        for (PCB p : queue) {
            System.out.println("进程名=>" + p.name);
            System.out.println("开始时刻=>" + p.startTime);
            System.out.println("完成时刻=>" + p.finishTime);
            System.out.println("等待时间=>" + (float)p.waitTime + "\n");
        }
    }
    
    /*在控制台输出三个队列*/
    public void printQueue() {
        System.out.println("每个时刻运行的进程");
        int time = 0;
        for (PCB p : run) {
            System.out.println(time++ + "---" + p.name);
        }
        System.out.println("\n每个时刻的就绪队列");
        time = 0;
        for (HashSet<PCB> pcbs : ready) {
            System.out.print(time++ + " === ");
            for (PCB value : pcbs) {
                System.out.print(value.name + " ");
            }
            System.out.println();
        }
        System.out.println("\n每个时刻的完成队列");
        time = 0;
        for (HashSet<PCB> pcbs : finish) {
            System.out.print(time++ + " === ");
            for (PCB value : pcbs) {
                System.out.print(value.name + " ");
            }
            System.out.println();
        }
    }
    
    /*进程运行*/
    public void processRun(ArrayList<PCB> workArr, double time) {
        int cpuTime = 0;
        boolean t = false;
        while (cpuTime < Math.ceil(time)) {
            int i;
            for (i = 0; i < workArr.size(); i++) {
                HashSet<PCB> readyMoment = new HashSet<>();
                HashSet<PCB> finishMoment = new HashSet<>();
                if (Objects.equals(workArr.get(i).state, "finish")) continue;
                if (workArr.get(i).startTime <= cpuTime) {
                    //这段时间内该进程一直在cpu上运行
                    workArr.get(i).state = "running";
                    //对完成时间向上取整
                    int preTime = cpuTime;
                    for (int j = 1; j <= Math.ceil(workArr.get(i).finishTime) - preTime; j++) {
                        run.add(workArr.get(i));
                        //生成这一时刻的就绪队列
                        for (PCB value : workArr) {
                            if (Objects.equals(value.state, "ready")) {
                                readyMoment.add(value);
                            }
                        }
                        ready.add(readyMoment); //插入总就绪队列中
                        //生成这一时刻的完成队列
                        for (PCB value : workArr) {
                            if (Objects.equals(value.state, "finish")) {
                                finishMoment.add(value);
                            }
                        }
                        finish.add(finishMoment);
                        cpuTime++;
                    }
                    workArr.get(i).state = "finish";
                    continue;
                }
                //在没有进程上cpu时，为ready每个时刻加上空进程名
                ready.add(readyMoment); //插入总就绪队列中
                finish.add(finishMoment);
                run.add(new PCB("null", -1, -1, -1));
                cpuTime++;
                i--;
            }
        }
    }
    
    /*生成工作序列，并返回运行结束时的时间（用于fcfs与ps算法中）*/
    public double buildWork(ArrayList<PCB> PCBArr) {
        double time = 0;
        for (PCB p : PCBArr) {
            if (workArr.size() == 0 || time < p.arriveTime) {
                p.startTime = p.arriveTime;
            } else {
                p.startTime = workArr.get(workArr.size()-1).finishTime;
            }
            p.finishTime = p.startTime + p.runTime;
            p.waitTime = p.startTime - p.arriveTime;
            time = p.finishTime;
            workArr.add(p);
        }
        return time;
    }
    
    /*生成周转时间和带权周转时间*/
    public void setTurnTime() {
        for (PCB p : pcb) {
            p.turnTime = p.finishTime - p.arriveTime;
            p.powerTime = p.turnTime / p.runTime;
        }
    }
}
