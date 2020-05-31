package org.sid.metier;

import java.util.Date;

import org.sid.dao.ClientRepository;
import org.sid.dao.CompteRepository;
import org.sid.dao.OperationRepository;
import org.sid.entities.Compte;
import org.sid.entities.CompteCourant;
import org.sid.entities.Operation;
import org.sid.entities.Retrait;
import org.sid.entities.Versement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service		//pour instancier cette classe au demarage
@Transactional
public class BanqueMetierImpl implements IBanqueMetier {
	@Autowired
	private CompteRepository compteRepository;
	@Autowired
	private OperationRepository operationRepository;
	@Autowired
	private ClientRepository clientRepository;
	
	@Override
	public Compte consulterCompte(String codeCpte) {
		Compte compte = compteRepository.findById(codeCpte).get();
		if(compte==null)
			throw new RuntimeException("Compte introuvable");
		return compte;
	}

	@Override
	public void verser(String codeCpte, double montant) {
		
		Compte compte = consulterCompte(codeCpte);
		Versement v = new Versement(new Date(), montant, compte);
		operationRepository.save(v);
		compte.setSolde(compte.getSolde() + montant);
		compteRepository.save(compte);
		
		
	}

	@Override
	public void retirer(String codeCpte, double montant) {
		Compte compte = consulterCompte(codeCpte);
		double facilitesCaisse = 0;
		if(compte instanceof CompteCourant)
			facilitesCaisse = ((CompteCourant) compte).getDecouvert();
		if(compte.getSolde() + facilitesCaisse <montant)
			throw new RuntimeException("Solde insuffisant");
		Retrait r = new Retrait(new Date(), montant, compte);
		operationRepository.save(r);
		compte.setSolde(compte.getSolde() - montant);
		compteRepository.save(compte);
		
		
	}

	@Override
	public void virement(String codeCpte1, String codeCpte2, double montant) {
		if(codeCpte1.equals(codeCpte2)) {
			throw new RuntimeException("Opération impossible");
		}else {
			retirer(codeCpte1, montant);
			verser(codeCpte2, montant);
		}
		
		
	}

	@Override
	public Page<Operation> listOperations(String codeCpte, int page, int size) {
		
		return operationRepository.listOperation(codeCpte, PageRequest.of(page, size));
		
	}

}
