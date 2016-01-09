import java.util.concurrent.{TimeUnit, Executors}
import org.openqa.selenium
import org.openqa.selenium.{WebElement, WebDriver, By}
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.{ExpectedConditions, ExpectedCondition, WebDriverWait}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConversions._
/**
  * Created by Alex on 1/7/2016.
  */
object IMDBAverageCalculator {
  def main (args: Array[String]) {
    val actor = "John Malkovich"
    implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(2))
    val driver:WebDriver = new FirefoxDriver()
    def waitUntilVisibleByXpath(xpath:String, timeOutInSeconds:Int):Unit={
      new WebDriverWait(driver, timeOutInSeconds).until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)))
    }
    driver.get("http://www.imdb.com/")
    driver.findElement(By.id("navbar-query")).sendKeys(actor)
    driver.findElement(By.id("navbar-submit-button")).click()
    waitUntilVisibleByXpath("//a[text()='" + actor + "']", 5)
    driver.findElement(By.xpath("//a[text()='" + actor +"']")).click()
    val films: List[Any] = driver.findElements(By.xpath("//div[@class='filmo-category-section'][1]/div[contains(@class, 'filmo-row')]/b/a")).toList
    val links:List[String] = films.map(film => film.asInstanceOf[WebElement].getAttribute("href"))
    val ratings: Future[List[Option[Double]]] = Future.traverse(links)(link => new RatingRetriever(executionContext).resolveFilmToRating(link))
    ratings.onFailure{
      case exception => println(exception.getMessage)
      driver.quit();
    }
    ratings.onSuccess{
      case l => {
        val numberOfRatings = l.filter(p => p.isDefined).size
        val totalRating = l.filter(p => p.isDefined).foldLeft(0d)((accumulator, element) => accumulator + element.get)
        println("There are " + numberOfRatings + " films with rating.")
        println("Total rating is " + totalRating)
        println("Average rating is: " + (totalRating / numberOfRatings))
        driver.quit();
      }
    }
  }
}
