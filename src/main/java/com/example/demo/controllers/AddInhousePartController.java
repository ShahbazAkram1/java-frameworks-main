package com.example.demo.controllers;

import com.example.demo.domain.InhousePart;
import com.example.demo.domain.Part;
import com.example.demo.service.InhousePartService;
import com.example.demo.service.InhousePartServiceImpl;
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

import javax.validation.Valid;

/**
 *
 */
@Controller
public class AddInhousePartController {
    @Autowired
    private ApplicationContext context;

    @GetMapping("/showFormAddInPart")
    public String showFormAddInhousePart(Model theModel) {
        InhousePart inhousepart = new InhousePart();
        theModel.addAttribute("inhousepart", inhousepart);
        return "InhousePartForm";
    }

    @PostMapping("/showFormAddInPart")
    public String submitForm(
            @Valid @ModelAttribute("inhousepart") InhousePart part,
            BindingResult theBindingResult,
            Model theModel) {

        theModel.addAttribute("inhousepart", part);

        if (part.getPartId() == 0) {
            PartService repo2 = context.getBean(PartServiceImpl.class);
            Part existingPart = repo2.findByName(part.getName());
            System.out.println("ExistingPart= " + existingPart);
            if (existingPart != null) {
                String alertMessage = "A part with this name already exists.";
                String script = String.format("alert('%s');", alertMessage);
                theModel.addAttribute("javascript", script); // Add the script to the model
                theBindingResult.rejectValue("name", "error.inhousepart", alertMessage);
                return "InhousePartForm";
            }
        }

        if (theBindingResult.hasErrors()) {
            return "InhousePartForm";
        } else {
            int inventory = part.getInv();
            int minInventory = part.getMinInventory();
            int maxInventory = part.getMaxInventory();

            if (inventory < minInventory || inventory > maxInventory) {
                theBindingResult.rejectValue("inv", "inventoryOutOfRange", "Inventory must be between min and max values");
                return "InhousePartForm";
            }

            InhousePartService repo = context.getBean(InhousePartServiceImpl.class);
            InhousePart ip = repo.findById((int) part.getId());
            if (ip != null) {
                ip.setName(part.getName());
                ip.setInv(part.getInv());
                ip.setMinInventory(part.getMinInventory());
                ip.setMaxInventory(part.getMaxInventory());
                repo.save(ip);
            } else {
                repo.save(part);
            }

            return "confirmationaddpart";
        }
    }


}