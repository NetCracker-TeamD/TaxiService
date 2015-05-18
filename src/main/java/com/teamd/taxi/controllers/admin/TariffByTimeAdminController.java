package com.teamd.taxi.controllers.admin;


import com.teamd.taxi.entity.TariffByTime;
import com.teamd.taxi.models.admin.AdminResponseModel;
import com.teamd.taxi.models.admin.TariffsByTimeModel;
import com.teamd.taxi.service.AdminPagesUtil;
import com.teamd.taxi.service.TariffService;
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
import javax.validation.Valid;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin")
public class TariffByTimeAdminController {

    private static final int DEFAULT_NUM_OF_RECORDS_ON_PAGE = 20;
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

    private static final String MESSAGE_TARIFF_ID_NOT_EXIST = "admin.tariff.delete.nonexistent";
    private static final String MESSAGE_SUCCESS_DELETE = "admin.tariff.delete.success";

    @Resource
    private Environment env;

    @Autowired
    private TariffService tariffService;

    @Autowired
    private AdminPagesUtil pagesUtil;

    //URL example: tariffs_by_time?page=1&order=model
    @RequestMapping(value = "/tariffs_by_time", method = RequestMethod.GET)
    public String viewTariffs(@Valid TariffsByTimeModel pageModel, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            //TODO: Return 404 here or other error page
            return "404";
        }

        Sort sort = new Sort(new Sort.Order(DEFAULT_SORT_DIRECTION, pageModel.getOrder()));;

        Page<TariffByTime> tariffs = tariffService.getTariffs(new PageRequest(pageModel.getPage(),DEFAULT_NUM_OF_RECORDS_ON_PAGE, sort));
        model.addAttribute("page", tariffs);

        ArrayList<Integer> pagination = pagesUtil.getPagination(pageModel.getPage(), tariffs.getTotalPages());
        model.addAttribute("pagination", pagination);

        return "admin/tariffsByTime";
    }

    //URL example: tariff_by_time-delete?id=5
    @RequestMapping(value = "/tariff_by_time-delete", method = RequestMethod.POST)
    @ResponseBody
    public AdminResponseModel<String> removeTariff(@RequestParam(value = "id") Integer id) {
        AdminResponseModel<String> response = new AdminResponseModel<>();
        System.out.println("In tariff delete with id = " + id);
        try {
            tariffService.removeTariff(id);
            response.setResult(AdminResponseModel.RESULT_SUCCESS);
            response.setContent(env.getRequiredProperty(MESSAGE_SUCCESS_DELETE));
        } catch (EmptyResultDataAccessException e) {
            //Error
            response.setResult(AdminResponseModel.RESULT_FAILURE);
            response.setContent(env.getRequiredProperty(MESSAGE_TARIFF_ID_NOT_EXIST));
        }
        return response;
    }

}

