import javax.inject._

import play.api.http.DefaultHttpFilters
import filters.ForceHttpsFilter

class Filters @Inject() (exampleFilter: ForceHttpsFilter) extends DefaultHttpFilters(exampleFilter)
