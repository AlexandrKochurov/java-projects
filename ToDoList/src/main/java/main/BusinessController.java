package main;

import main.model.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import main.model.Business;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class BusinessController {
    @Autowired
    private BusinessRepository businessRepository;

    @RequestMapping(value = "/business/", method = RequestMethod.GET)
    public List<Business> list(){
        Iterable<Business> businessIterable = businessRepository.findAll();
        ArrayList<Business> businesses = new ArrayList<>();
        for(Business business: businessIterable){
            businesses.add(business);
        }
        return businesses;
    }

    @RequestMapping(value = "/business/{id}/", method = RequestMethod.GET)
    public ResponseEntity oneBusiness(@PathVariable Integer id){
        Optional<Business> optionalBusiness = businessRepository.findById(id);
        return optionalBusiness.map(business -> new ResponseEntity(business, HttpStatus.OK)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @RequestMapping(value = "/business/", method = RequestMethod.POST)
    public int putBusiness(Business business){
        Business newBusiness = businessRepository.save(business);
        return newBusiness.getId();
    }

    @RequestMapping(value = "/business/", method = RequestMethod.DELETE)
    public void deleteBusiness(Business business){
        businessRepository.delete(business);
    }

    @RequestMapping(value = "/business/all/", method = RequestMethod.DELETE)
    public void deleteAllBusiness(){
        businessRepository.deleteAll();
    }
}
