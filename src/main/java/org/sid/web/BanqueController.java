package org.sid.web;

import org.springframework.ui.Model;
import org.sid.entities.Client;
import org.sid.entities.Compte;
import org.sid.entities.Operation;
import org.sid.metier.IBanqueMetier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



@Controller				//springmvc
public class BanqueController {
	@Autowired
	private IBanqueMetier banqueMetier;
	
	@RequestMapping("/operations")
	public String index() {
		return "comptes";
	}
	
	@RequestMapping("/consulterCompte")
	public String consulter(Model model , String codeCompte ,@RequestParam(name="page",defaultValue = "0") int page) {
		model.addAttribute("codeCompte", codeCompte);
		Page<Operation> pageOperations = banqueMetier.listOperations(codeCompte, page, 5);
		int[] pages = new int[pageOperations.getTotalPages()];
		model.addAttribute("pages", pages);
		model.addAttribute("listOperations", pageOperations.getContent());		//retourne liste des operations de la page
		try {
		 Compte cpt = banqueMetier.consulterCompte(codeCompte);
		 model.addAttribute("compte", cpt);
		}catch(Exception e) {
			model.addAttribute("exception", e);
		}
		
		return "comptes";
	}
	
	@RequestMapping(value="/saveOperation",method=RequestMethod.POST)
	public String saveOperation(Model model , String typeOperation , String codeCompte , String codeCompte2 , double montant) {
		try {
			if(typeOperation.equals("VERS")) {
				banqueMetier.verser(codeCompte, montant);
			}
			else if(typeOperation.equals("RET")) {
				banqueMetier.retirer(codeCompte, montant);
			}
			else if(typeOperation.equals("VIR")) {
				banqueMetier.virement(codeCompte, codeCompte2, montant);
			}
			
		}catch(Exception e) {
			model.addAttribute("error", e);
			return "redirect:/consulterCompte?codeCompte="+codeCompte+"&error="+e.getMessage();
		}
		return "redirect:/consulterCompte?codeCompte="+codeCompte;
	}
	
	
	
}
