package com.teamd.taxi.controllers.admin;

import com.teamd.taxi.entity.*;
import com.teamd.taxi.persistence.repository.TariffByTimeRepository;
import com.teamd.taxi.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
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
        model.addAttribute("tariffs", tariffByTimes);
        return "admin/tariffsByTime";
    }

    @RequestMapping(value = "/byTime/update", produces = "application/json",
            method = RequestMethod.POST, consumes = "application/json")
    public
    @ResponseBody
    String updateTariffByTime(BufferedReader reader) throws IOException {
        String line;
        List<String> items = new ArrayList<String>();
        while ((line = reader.readLine()) != null) {
            String lines[] = line.split(",");
            for (int i = 0; i < lines.length; i++) {
                String part[] = lines[i].split("\"");
                if (!part[part.length - 1].equals("}")) {
                    items.add(part[part.length - 1]);
                } else {
                    items.add(part[part.length - 2]);
                }
            }
        }
        int id = Integer.parseInt(items.get(0));
        String type = items.get(1);
        float price = Float.parseFloat(items.get(2));
        String from = items.get(3);
        String to = items.get(4);
        TariffByTime entry = tariffByService.findOne(id);
        entry.setPrice(price);
        tariffByService.save(entry);
        return "{\"status\" : \"success\"}";
    }


    @RequestMapping(value = "/byTime/remove", produces = "application/json",
            method = RequestMethod.POST, consumes = "application/json")
    public
    @ResponseBody
    String removeTariffByTime(BufferedReader reader) throws IOException {
        String line;
        List<String> items = new ArrayList<String>();
        while ((line = reader.readLine()) != null) {
            String lines[] = line.split(",");
            for (int i = 0; i < lines.length; i++) {
                String part[] = lines[i].split("\"");
                if (!part[part.length - 1].equals("}")) {
                    items.add(part[part.length - 1]);
                } else {
                    items.add(part[part.length - 2]);
                }
            }
        }
        int id = Integer.parseInt(items.get(0));
        String type = items.get(1);
        float price = Float.parseFloat(items.get(2));
        String from = items.get(3);
        String to = items.get(4);
        TariffByTime entry = tariffByService.findOne(id);
        tariffByService.removeTariff(entry.getId());
        return "{\"status\" : \"success\"}";
    }


    private int convertStringDayToInt(String day) {
        switch (day) {
            case "MONDAY":
                return Calendar.MONDAY;
            case "TUESDAY":
                return Calendar.TUESDAY;
            case "WEDNESDAY":
                return Calendar.WEDNESDAY;
            case "THURSDAY":
                return Calendar.THURSDAY;
            case "FRIDAY":
                return Calendar.FRIDAY;
            case "SUNDAY":
                return Calendar.SUNDAY;
            default:
                return Calendar.SATURDAY;
        }
    }

    @RequestMapping(value = "/byTime/create", produces = "application/json",
            method = RequestMethod.POST, consumes = "application/json")
    public
    @ResponseBody
    String createTariffByTime(BufferedReader reader) throws IOException, ParseException {
        String line;
        List<String> items = new ArrayList<String>();
        while ((line = reader.readLine()) != null) {
            String lines[] = line.split(",");
            for (int i = 0; i < lines.length; i++) {
                String part[] = lines[i].split("\"");
                if (!part[part.length - 1].equals("}")) {
                    items.add(part[part.length - 1]);
                } else {
                    items.add(part[part.length - 2]);
                }
            }
        }
        String type = items.get(0);
        float price = Float.parseFloat(items.get(1));
        String from = items.get(3);
        String to = items.get(2);
        TariffByTime entry = new TariffByTime();
        entry.setPrice(price);
        if (type.equals("DAY_OF_WEEK")) {
            entry.setTariffType(TariffType.DAY_OF_WEEK);
            Calendar fromCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            fromCalendar.set(Calendar.DAY_OF_WEEK, convertStringDayToInt(from));
            endCalendar.set(Calendar.DAY_OF_WEEK, convertStringDayToInt(to));
            entry.setFrom(fromCalendar);
            entry.setTo(endCalendar);
        } else if (type.equals("DAY_OF_YEAR")) {
            entry.setTariffType(TariffType.DAY_OF_YEAR);
            Calendar fromCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            fromCalendar.setTime(sdf.parse(from));
            endCalendar.setTime(sdf.parse(to));
            entry.setFrom(fromCalendar);
            entry.setTo(endCalendar);
        } else {
            entry.setTariffType(TariffType.TIME_OF_DAY);
            Calendar fromCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.US);
            fromCalendar.setTime(sdf.parse(from));
            endCalendar.setTime(sdf.parse(to));
            entry.setFrom(fromCalendar);
            entry.setTo(endCalendar);
        }
        System.out.println(entry);
        tariffByService.save(entry);
        return "{\"status\" : \"success\"}";
    }


    private boolean isExists(TariffByTime entry) {
        List<TariffByTime> tariffs = tariffByService.findTariffsByType(entry.getTariffType());
        if (tariffs.contains(entry)) {
            return true;
        }
        for (TariffByTime tariff : tariffs) {
            if (tariff.getFrom().after(entry.getFrom())) {

            }
        }
        return false;
    }

    private void isDayOfWeek() {

    }

    private void isDayOfYear() {

    }

    private void isTimeOfDay(TariffByTime entry) {
      //  List<TariffByTime> tariffByTimes =
    }


}
