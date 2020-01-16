package com.aws.healthcheck.web;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.jb.commissions.dto.CommissionType;
import com.jb.commissions.dto.T24Response;
import com.jb.commissions.services.CommissionsService;
import com.jb.commissions.services.FileHandlingService;

@Controller
public class MainController {

	@Autowired
	private HealthService healthService;

	@Autowired
	private FileHandlingService fileHandlingService;

	@GetMapping("/")
	public String home(final Model model, final Principal principal) throws IOException {

		Response<Resource> data = commissionService.check();
		fileHandlingService.writeToFile(data);
		return "home";
	}

}
