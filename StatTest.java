
public class StatTest
{
  public static void main(String [] args)
  {
    Stat.incr("myvar.incr");
    Stat.count("myvar.count",5);
    Stat.scalecount("myvar.scalecount",5,2);
    Stat.gauge("myvar.gauge",33);
    Stat.gauge("myvar.gauge",55);
    Stat.gauge("myvar.gauge",77);
  }
}
