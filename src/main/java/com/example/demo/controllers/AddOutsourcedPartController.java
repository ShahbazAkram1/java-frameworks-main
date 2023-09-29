package com.example.demo.controllers;

import com.example.demo.domain.InhousePart;
import com.example.demo.domain.OutsourcedPart;
import com.example.demo.domain.Part;
import com.example.demo.service.OutsourcedPartService;
import com.example.demo.service.OutsourcedPartServiceImpl;
import com.example.demo.service.PartService;
import com.example.demo.service.PartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 *
 *
 *
 *
 */
@Controller
public class AddOutsourcedPartController {
    @Autowired
    private ApplicationContext context;

    @GetMapping("/showFormAddOutPart")
    public String showFormAddOutsourcedPart(Model theModel){
        Part part=new OutsourcedPart();
        theModel.addAttribute("outsourcedpart",part);
        return "OutsourcedPartForm";
    }

    @PostMapping("/showFormAddOutPart")
    public String submitForm(@Valid @ModelAttribute("outsourcedpart") OutsourcedPart part, BindingResult bindingResult, Model theModel) {
        theModel.addAttribute("outsourcedpart", part);
        PartService repo2 = context.getBean(PartServiceImpl.class);
        Part existingPart = repo2.findByName(part.getName());
        System.out.println("ExistingPart= " + existingPart);
        if (existingPart != null) {
            String alertMessage = "A part with this name already exists.";
            String script = String.format("alert('%s');", alertMessage);
            theModel.addAttribute("javascript", script); // Add the script to the model
            bindingResult.rejectValue("name", "error.outsourcepart", alertMessage);
            return "OutsourcedPartForm";
        }
        if (bindingResult.hasErrors()) {
            return "OutsourcedPartForm";
        } else {
            int inventory = part.getInv();
            int minInventory = part.getMinInventory();
            int maxInventory = part.getMaxInventory();

            if (inventory < minInventory || inventory > maxInventory) {
                bindingResult.rejectValue("inv", "inventoryOutOfRange", "Inventory must be between min and max values");
                return "OutsourcedPartForm";
            }

            OutsourcedPartService repo = context.getBean(OutsourcedPartServiceImpl.class);
            OutsourcedPart op = repo.findById((int) part.getId());
            if (op != null) {
                part.setProducts(op.getProducts());
            }
            repo.save(part);

            return "confirmationaddpart";
        }
    }


}