package files;

import java.text.DecimalFormat;

public class Timer
{
  public static final int inSec = 0;
  public static final int inMin = 1;
  public static final int inBoth = 2;
  private Long initialStartTime = null;
  private Long startTime = null;
  private Long endTime = null;
  
  public void reset()
  {
    this.startTime = Long.valueOf(System.currentTimeMillis());
    if (this.initialStartTime == null) {
      this.initialStartTime = this.startTime;
    }
  }
  
  public void stop()
  {
    if (this.endTime == null) {
      this.endTime = Long.valueOf(System.currentTimeMillis());
    }
  }
  
  public long elapsed()
  {
    if (this.endTime == null) {
      return System.currentTimeMillis() - this.startTime.longValue();
    }
    return this.endTime.longValue() - this.startTime.longValue();
  }
  
  public long totalElapsed()
  {
    if (this.endTime == null) {
      return System.currentTimeMillis() - this.initialStartTime.longValue();
    }
    return this.endTime.longValue() - this.initialStartTime.longValue();
  }
  
  public String print(int timeSelection)
  {
    long timeTakenInSec = elapsed() / 1000L;
    DecimalFormat format = new DecimalFormat("0.000");
    String s = "";
    switch (timeSelection)
    {
    case 0: 
      s = format.format(timeTakenInSec) + "secs";
      break;
    case 1: 
      s = (int)(timeTakenInSec / 60.0D) + "mins";
      break;
    case 2: 
      s = s + format.format(timeTakenInSec) + "secs ";
      s = s + "(=" + (int)(timeTakenInSec / 60.0D) + "mins)";
    }
    return s;
  }
}
