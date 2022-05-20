package process;

public class PCB {
    public String name; //进程标识符
    public double arriveTime; //到达时间
    public double runTime; //需要的运行时间
    public int priority; //进程优先数
    public double startTime; //开始时间
    public double waitTime; //等待时间
    public double finishTime; //完成时间
    public double turnTime; //周转时间
    public double powerTime; //带权周转时间
    public String state; //进程的状态
    //public int round; //进程轮转时间片
    public double cpuTime; //进程占用 CPU 时间
    public int count; //计数器
    //public int needTime; //进程到完成还要的 CPU 时间
    
    PCB() {}
    
    public PCB(String name, double arriveTime, double runTime, int priority) {
        this.name = name;
        this.arriveTime = arriveTime;
        this.runTime = runTime;
        this.priority = priority;
        
        this.count = 0;
        this.state = "ready";
    }
}
