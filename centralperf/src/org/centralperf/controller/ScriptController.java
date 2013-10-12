package org.centralperf.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.centralperf.model.Run;
import org.centralperf.model.Script;
import org.centralperf.model.ScriptVersion;
import org.centralperf.repository.ProjectRepository;
import org.centralperf.repository.RunRepository;
import org.centralperf.repository.ScriptRepository;
import org.centralperf.service.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionAttributes
public class ScriptController {
	
	@Resource
	private ScriptRepository scriptRepository;

	@Resource
	private ScriptService scriptService;
	
	@Resource
	private RunRepository runRepository;

    @Resource
    private ProjectRepository projectRepository;

	private static final Logger log = LoggerFactory.getLogger(ScriptController.class);

    @ExceptionHandler(Throwable.class)
    public @ResponseBody String handleValidationFailure(Throwable exception) {
        return exception.getMessage();
    }

    @RequestMapping(value = "/project/{projectId}/script/new", method = RequestMethod.GET)
    public String createScript(@PathVariable("projectId") Long projectId, Model model) {
        model.addAttribute("newScript",new Script());
        model.addAttribute("project",projectRepository.findOne(projectId));
        return "macros/script/new-script-form.macro";
    }

    @RequestMapping(value = "/project/{projectId}/script/new", method = RequestMethod.POST)
    public String addScript(
                            @PathVariable("projectId") Long projectId,
    						@ModelAttribute("script") Script script,
                            @RequestParam("jmxFile") MultipartFile file,
                            BindingResult result) {
        ScriptVersion scriptVersion = new ScriptVersion();

		// Get the jmx File
		try {
			String jmxContent = new String(file.getBytes());
            scriptVersion.setJmx(jmxContent);
		} catch (IOException e) {
		}
        scriptVersion.setNumber(1L);
        scriptVersion.setDescription("First version");
        script.getVersions().add(0, scriptVersion);
		scriptService.addScript(script);
        return "redirect:/project/" + projectId + "/script/" + script.getId() + "/detail";
    }
     
    @RequestMapping("/project/{projectId}/script")
    public String showProjectScripts(@PathVariable("projectId") Long projectId, Model model) {
        model.addAttribute("newScript",new Script());
        model.addAttribute("scripts",scriptRepository.findAll());
        model.addAttribute("project",projectRepository.findOne(projectId));
        return "scripts";
    }
    
    @RequestMapping(value="/project/{projectId}/script/{id}/detail", method = RequestMethod.GET)
    public String showScriptDetail(
            @PathVariable("id") Long id,
            @PathVariable("projectId") Long projectId,
            Model model){
    	log.debug("script details for script ["+id+"]");
    	model.addAttribute("script",scriptRepository.findOne(id));
        model.addAttribute("project",projectRepository.findOne(projectId));
        return "scriptDetail";
    }    
    
	@RequestMapping(value = "/project/{projectId}/script/{id}/delete", method = RequestMethod.GET)
    public String deleteScript(
            @PathVariable("id") Long id,
            @PathVariable("projectId") Long projectId,
            RedirectAttributes redirectAttrs) {
		List<Run> runsAttached = runRepository.findByScriptVersionScriptId(id);
		if(!runsAttached.isEmpty()){
			redirectAttrs.addFlashAttribute("error","Unable to delete this script because it's attached to at least one run");
		} else{
			scriptRepository.delete(id);
		}
        return "redirect:/project/" + projectId + "/script";
    }

    @RequestMapping("/script")
    public String showScripts(Model model) {
        model.addAttribute("scripts",scriptRepository.findAll());
        return "scripts";
    }
}