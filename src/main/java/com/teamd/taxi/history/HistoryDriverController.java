package com.teamd.taxi.history;

import java.sql.Date;
import java.sql.Time;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

import com.teamd.taxi.entity.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/driver/")
public class HistoryDriverController {
	/*org.apache.log4j.Logger logger= org.apache.log4j.Logger.getLogger("My");
            logger.info("id:"+ id);*/
	TaxiOrder taxiOrder;
	@RequestMapping(value="history/", method =RequestMethod.GET)
	public String viewHistory(Model model,HttpServletRequest  request){
		//request.getParameter("sort")
		//request.getParameter("page")
		int numberOfRows=5;
		List<Route>  routeList;
		routeList = getListRoute(1	, "filter");
		model.addAttribute("routesList", routeList);
		model.addAttribute("pages",2);
		return "drv-history";
	}
	@SuppressWarnings("deprecation")
	List<Route> getListRoute(int pageNumber,String filter){

		Route route;
		List<Route> routeList=new ArrayList<Route>();
		TaxiOrder taxiOrder;
		Calendar cal=Calendar.getInstance(Locale.ENGLISH);
		for(int i=1;i<=7;i++){
			taxiOrder=new TaxiOrder();
			taxiOrder.setId((long)i);
			route=new Route();
			//init taxiorder
			User user=new User();
			user.setFirstName("Anton" + i);
			user.setPhoneNumber("063538702" + i);
			taxiOrder.setComment("Nice");
			taxiOrder.setCustomer(user);
			PaymentType paymentType=PaymentType.CASH;
			taxiOrder.setPaymentType(paymentType);
			taxiOrder.setExecutionDate(new Date(Calendar.getInstance(Locale.ENGLISH).getTimeInMillis()));
			List<Feature> features=new ArrayList<Feature>();
			Feature feature=new Feature();
			feature.setId(1);
			feature.setName("WiFi");
			features.add(feature);
			feature=new Feature();
			feature.setId(2);
			feature.setName("Animal transportation");
			features.add(feature);
			feature=new Feature();
			feature.setId(3);
			feature.setName("Smoking driver");
			features.add(feature);
			feature=new Feature();
			feature.setId(4);
			feature.setName("Air-conditioner");
			features.add(feature);
			taxiOrder.setFeatures(features);
			route.setId((long)i);
			route.setDestinationAddress("бул. Лесі Українки, 14, Київ, Украина");
			route.setSourceAddress("вул. Круглоуніверситетська, 9 Київ, Украина");
			route.setDistance(1.8F);
			route.setCompletionTime(new Date(Calendar.getInstance(Locale.ENGLISH).getTimeInMillis() + 1001100));
			route.setStartTime(new Time(Calendar.getInstance(Locale.ENGLISH).getTimeInMillis()));
			route.setStatus("completed");
			route.setOrder(taxiOrder);
			route.setTotalPrice(150F);
			routeList.add(route);
		}
		return routeList;
	}
}
