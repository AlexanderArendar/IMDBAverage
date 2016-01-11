import org.openqa.selenium.firefox.FirefoxDriver

/**
  * Created by Alex on 1/11/2016.
  */
class FireFoxThread(r:Runnable) extends Thread(r:Runnable){
  val driver = new FirefoxDriver

  override def interrupt()={
    driver.quit
    super.interrupt
  }
}
