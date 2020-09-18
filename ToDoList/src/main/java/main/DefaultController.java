package main;

import main.model.Business;
import main.model.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
public class DefaultController {
    @Autowired
    BusinessRepository businessRepository;

    @RequestMapping("/")
    public String index(Model model) {
        Iterable<Business> businessIterable = businessRepository.findAll();
        ArrayList<Business> businesses = new ArrayList<Business>();
        for(Business business: businessIterable){
            businesses.add(business);
        }
        model.addAttribute("businesses", businesses);
        model.addAttribute("bcount", businesses.size());
        return "ToDoList";
    }
}
