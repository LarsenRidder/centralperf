package org.centralperf.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.centralperf.model.Run;
import org.centralperf.model.Script;
import org.centralperf.repository.RunRepository;
import org.centralperf.repository.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionAttributes
public class ScriptController {
	
	@Autowired
	private ScriptRepository scriptRepository;
	
	@Resource
	private RunRepository runRepository;	
	
	@RequestMapping(value = "/script/new", method = RequestMethod.POST)
    public String addScript(
    						@ModelAttribute("script") Script script,
                            @RequestParam("jmxFile") MultipartFile file,
                            BindingResult result) {
		// Get the jmx File
		try {
			String jmxContent = new String(file.getBytes());
			script.setJmx(jmxContent);
			scriptRepository.save(script);			
		} catch (IOException e) {
		}

        return "redirect:/script";
    }
     
    @RequestMapping("/script")
    public String showScripts(Model model) {
    	model.addAttribute("scripts",scriptRepository.findAll());
    	model.addAttribute("command",new Script());
        return "scripts";
    }
    
	@RequestMapping(value = "/script/{id}/delete", method = RequestMethod.GET)
    public String deleteScript(@PathVariable("id") Long id, RedirectAttributes redirectAttrs) {
		List<Run> runsAttached = runRepository.findByScriptId(id);
		if(!runsAttached.isEmpty()){
			redirectAttrs.addFlashAttribute("error","Unable to delete this script because it's attached to at least one run");
		} else{
			scriptRepository.delete(id);
		}
        return "redirect:/script";
    }    
   
}
