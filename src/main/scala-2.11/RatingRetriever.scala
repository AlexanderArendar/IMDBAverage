import java.util.concurrent.{Executors, TimeUnit}

import org.openqa.selenium.{WebElement, By, WebDriver}
import org.openqa.selenium.firefox.FirefoxDriver
import scala.collection.JavaConversions._
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Alex on 1/9/2016.
  */
class RatingRetriever(context:ExecutionContext) {

  def resolveFilmToRating(url:String):Future[Option[Double]]={
    Future{
      println(Thread.currentThread().getName)
      val driver: WebDriver = new FirefoxDriver()
      driver.get(url)
      driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS)
      val ratingBoxes = driver.findElements(By.xpath("//div[contains(@class, 'star-box')]/div[contains(@class, 'star-box-giga-star')]"))
      driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS)
      ratingBoxes.size match {
        case 0 =>{
          println("None")
          driver.quit();
          None
        }
        case _ =>{
          val ratingBox:WebElement = ratingBoxes(0)
          val rating:Double = ratingBox.getText.toDouble
          println(rating)
          driver.quit();
          Some(rating)
        }
      }
    }(context)
  }
}
