package com.teamd.taxi.controllers.admin;

import com.teamd.taxi.entity.*;
import com.teamd.taxi.persistence.repository.TariffByTimeRepository;
import com.teamd.taxi.service.CarClassService;
import com.teamd.taxi.service.FeatureService;
import com.teamd.taxi.service.ServiceTypeService;
import com.teamd.taxi.service.TariffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Anton on 20.05.2015.
 */
@Controller
@RequestMapping("/admin/tariffs")
public class TariffsAdminController {
    @Autowired
    private CarClassService carClassService;

    @Autowired
    private FeatureService featureService;

    @Autowired
    private ServiceTypeService serviceTypeService;

    @Autowired
    private TariffService tariffByService;

    @RequestMapping("/cars")
    public String viewTariffsOnCars(Model model) {
        List<CarClass> carClasses = carClassService.findAll(new Sort(Sort.Direction.ASC, "id"));
        model.addAttribute("carClasses", carClasses);
        return "admin/tariffsOnCar";
    }

    @RequestMapping(value = "/cars/update", method = RequestMethod.POST, consumes = "application/json")
    public
    @ResponseBody
    String updateCar(@RequestBody CarClass cc) {
        CarClass carClass = carClassService.findById(cc.getId());
        carClass.setIdlePriceCoefficient(cc.getIdlePriceCoefficient());
        carClass.setPriceCoefficient(cc.getPriceCoefficient());
        carClassService.save(carClass);
        return "{\"status\" : \"success\"}";
    }

    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public String viewTariffsOnSerivceType(Model model) {
        List<ServiceType> serviceTypes = serviceTypeService.findAll(new Sort(Sort.Direction.ASC, "id"));
        model.addAttribute("services", serviceTypes);
        return "admin/tariffsOnServiceType";
    }

    @RequestMapping(value = "/services/update", method = RequestMethod.POST, consumes = "application/json")
    public
    @ResponseBody
    String updateServiceType(@RequestBody ServiceType st) {
        ServiceType serviceType = serviceTypeService.findById(st.getId());
        serviceType.setMinPrice(st.getMinPrice());
        serviceType.setPriceByDistance(st.getPriceByDistance());
        serviceType.setPriceByTime(st.getPriceByTime());
        serviceTypeService.save(serviceType);
        return "{\"status\" : \"success\"}";
    }

    @RequestMapping(value = "/features", method = RequestMethod.GET)
    public String viewTariffsOnFeature(Model model) {
        List<Feature> featureTypes = featureService.findAll(new Sort(Sort.Direction.ASC, "id"));
        model.addAttribute("features", featureTypes);
        return "admin/tariffsOnFeature";
    }

    @RequestMapping(value = "/features/update", method = RequestMethod.POST, consumes = "application/json")
    public
    @ResponseBody
    String updateTariffsOnFeature(@RequestBody Feature f) {
        Feature feature = featureService.findById(f.getId());
        feature.setPrice(f.getPrice());
        featureService.save(feature);
        return "{\"status\" : \"success\"}";
    }


    @RequestMapping(value = "/byTime", method = RequestMethod.GET)
    public String viewTariffsByTime(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        int numberOfRecords = 10;
        if (page != 0) page--;
        Pageable pageable = new PageRequest(page, numberOfRecords, Sort.Direction.ASC, "id");
        Page<TariffByTime> tariffByTimes = tariffByService.getTariffs(pageable);
        model.addAttribute("tariffs", tariffByTimes.getContent());
        return "admin/tariffsByTime";
    }

    @RequestMapping(value = "/byTime/update", method = RequestMethod.POST, consumes = "application/json")
    public
    @ResponseBody
    String updateTariffsByTime(@RequestBody TariffByTime tbt) {
        TariffByTime tariffByTime = tariffByService.findOne(tbt.getId());
        tariffByTime.setFrom(tbt.getFrom());
        tariffByTime.setTo(tbt.getTo());
        tariffByTime.setPrice(tbt.getPrice());
        tariffByService.save(tariffByTime);
        return "{\"status\" : \"success\"}";
    }


    @ResponseBody
    @RequestMapping(value = "byTime/remove")
    public String removeTariffsByTime(@RequestBody TariffByTime tbt) {
        TariffByTime tariffByTime = tariffByService.findOne(tbt.getId());
        tariffByService.removeTariff(tariffByTime.getId());
        return "{\"status\" : \"success\"}";
    }

    @RequestMapping(value = "/byTime/create", method = RequestMethod.POST, consumes = "application/json")
    public
    @ResponseBody
    String createTariffsByTime(@RequestBody TariffByTime tbt) {
        TariffByTime newTariff = new TariffByTime();
        newTariff.setFrom(tbt.getFrom());
        newTariff.setTo(tbt.getTo());
        newTariff.setPrice(tbt.getPrice());
        tariffByService.save(newTariff);
        return "{\"status\" : \"success\"}";
    }

}
