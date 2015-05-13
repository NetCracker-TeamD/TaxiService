package com.teamd.taxi.controllers.admin;


import com.teamd.taxi.entity.CarClass;
import com.teamd.taxi.entity.Feature;
import com.teamd.taxi.entity.ServiceType;
import com.teamd.taxi.entity.TariffByTime;
import com.teamd.taxi.models.admin.AdminResponseModel;
import com.teamd.taxi.models.admin.TariffsByTimeModel;
import com.teamd.taxi.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class FeatureTariffAdminController {

    @Autowired
    private FeatureService featureService;

    @RequestMapping(value = "/feature_tariffs", method = RequestMethod.GET)
    public String viewTariffs(Model model, HttpServletRequest request) {


        //Sort sort = new Sort(new Sort.Order(DEFAULT_SORT_DIRECTION, pageModel.getOrder()));;

        List<Feature> features = featureService.getFeatures();
        model.addAttribute("features", features);


        return "admin/featureTariffs";
    }

}

