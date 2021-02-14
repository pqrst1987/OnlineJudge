package com.example.demo.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.SampleName;
import com.example.demo.model.SampleResult;
import com.example.demo.model.SiteUser;
import com.example.demo.model.SpringCamp;
import com.example.demo.model.TimeLimit;
import com.example.demo.model.TotalResult;
import com.example.demo.repository.SampleNameRepository;
import com.example.demo.repository.SampleResultRepository;
import com.example.demo.repository.SiteUserRepository;
import com.example.demo.repository.SpringCampRepository;
import com.example.demo.repository.TestCasePathRepository;
import com.example.demo.repository.TimeLimitRepository;
import com.example.demo.repository.TotalResultRepository;
import com.example.demo.util.Role;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
public class HomeController {

	private final SiteUserRepository siteUserRepository;
	private final SampleNameRepository sampleNameRepository;
	private final TotalResultRepository totalResultRepository;
	private final SampleResultRepository sampleResultRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final SpringCampRepository springCampRepository;
	private final TestCasePathRepository testCasePathRepository;
	private final TimeLimitRepository timeLimitRepository;

	public static String StorageRoot = "/storage";
	public static String TestCaseRoot = "/testcase";

	public void generateSourceFile(String SourceCode, long submissionId) throws IOException, InterruptedException {
		String sourceCodeName = StorageRoot + "/s" + String.valueOf(submissionId) + ".cpp";
		FileWriter SourceCodeWriter = new FileWriter(sourceCodeName);
		SourceCodeWriter.write(SourceCode);
		SourceCodeWriter.close();
		Process proc = Runtime.getRuntime().exec("chmod 711 " + sourceCodeName);
		proc.waitFor();
		proc.destroy();
	}

	static String getBatchFileName(long submissionId){
		String batchFileName = TestCaseRoot + "/b" + String.valueOf(submissionId) + ".sh";
		return batchFileName;
	}

	static int execScript(String script, String batchFileName) throws IOException, InterruptedException{
		File f = new File(batchFileName);
		FileWriter fw = new FileWriter(f);
		fw.write(script);
		fw.close();
		Process proc = Runtime.getRuntime().exec("chmod 777 " + batchFileName);
		proc.waitFor();
		proc.destroy();
		var processBuilder = new ProcessBuilder("sh", "-c", batchFileName);
		processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
		var process = processBuilder.start();
		int result = process.waitFor();
		process.destroy();
		f.delete();
		return result;
	}

	static void deleteFile(String file) throws IOException, InterruptedException{
		File f = new File(file);
		f.delete();
	}

	public int getResult(String testCaseName, String testCasePath, long submissionId, String execFileName, String problemName) throws IOException, InterruptedException{
		String execDirectory = TestCaseRoot + testCasePath + "/" + testCaseName;
		String inputDataFile = execDirectory + "/" + testCaseName + ".in";
		String outputDataFile = execDirectory + "/o" + String.valueOf(submissionId) + "_"+ testCaseName + ".dat";
		String rightAnswerFile = execDirectory + "/" + testCaseName + ".out";

		File f = new File(outputDataFile);
		f.createNewFile();
		ArrayList<TimeLimit> list = (ArrayList<TimeLimit>) timeLimitRepository.findByProblemName(problemName);
		String script2 = "timeout " + list.get(0).getTimeLimit() + " /root/commands/execio " + execFileName + " "+ inputDataFile + " " + outputDataFile;
		int runtimeResult = execScript(script2, getBatchFileName(submissionId));
		if(runtimeResult != 0) {
			deleteFile(outputDataFile);
			if(runtimeResult == 124) return 4;
			else return 3;
		}
		String script3 = "diff -q " + outputDataFile + " " + rightAnswerFile;
		int compareResult = execScript(script3, getBatchFileName(submissionId));
		if(compareResult == 0) {
			deleteFile(outputDataFile);
			return 1;
		}else {
			deleteFile(outputDataFile);
			return 2;
		}
	}

	public ArrayList<SampleResult> getSampleResultList(ArrayList<SampleName> list, String testCasePath, long submissionId, String user, String problemName) throws IOException, InterruptedException{
		ArrayList<SampleResult> sampleResultList = new ArrayList<SampleResult>();
		String sourceCodeName = StorageRoot + "/s" + String.valueOf(submissionId) + ".cpp";
		String execFileName = TestCaseRoot + testCasePath + "/e" + String.valueOf(submissionId) + ".exe";
		String batchFile = getBatchFileName(submissionId);
		String script = "g++ " + sourceCodeName + " -o " + execFileName;
		execScript(script, batchFile);
		File execFile = new File(execFileName);
		if(execFile.exists() == false) {
			sampleResultList.add(new SampleResult(-1L, submissionId, user, 5));
			return sampleResultList;
		}
		Process proc = Runtime.getRuntime().exec("chmod 777 " + execFileName);
		proc.waitFor();
		proc.destroy();
		for(int i = 0; i < list.size(); i++) {
			int result = getResult(list.get(i).getSampleName(), testCasePath, submissionId, execFileName, problemName);
			sampleResultList.add(new SampleResult(list.get(i).getSampleId(), submissionId, list.get(i).getSampleName(), result));
		}
		deleteFile(execFileName);
		return sampleResultList;
	}

	private String getSource(String sourceId) throws IOException {
		String InputFile = StorageRoot + "/s" + sourceId + ".cpp";
		String str = Files.lines(Paths.get(InputFile), Charset.forName("UTF-8"))
		        .collect(Collectors.joining(System.getProperty("line.separator")));
		return str;
	}

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/about")
	public String about() {
		return "about_me/about";
	}

	@GetMapping("/links")
	public String links() {
		return "links/links";
	}

	@GetMapping("/oj_entrance")
	public String oj_entrance() {
		return "judge_system/oj_entrance";
	}

	@GetMapping("/judge_system/spring_camp/choose")
	public String choose() {
		return "judge_system/spring_camp/choose";
	}

	@GetMapping("/login")
	public String login() {
		return "user_management/login";
	}

	@GetMapping("/user")
	public String showList(Authentication loginUser, Model model) {
		model.addAttribute("username", loginUser.getName());
		model.addAttribute("role", loginUser.getAuthorities());
		return "user_management/user";
	}

	@GetMapping("/list")
	public String showAdminList(Model model) throws IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usr = auth.getName();
		ArrayList<TotalResult> list = (ArrayList<TotalResult>) totalResultRepository.findByUser(usr);
		model.addAttribute("resultList", list);
		return "user_management/list";
	}

	@GetMapping("/register")
	public String register(@ModelAttribute("user") SiteUser user) {
		return "user_management/register";
	}

	@PostMapping("/register")
	public String process(@Validated @ModelAttribute("user") SiteUser user,
			BindingResult result, HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {

		if (result.hasErrors()) {
			return "user_management/register";
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if (user.isAdmin()) {
			user.setRole(Role.ADMIN.name());
		} else {
			user.setRole(Role.USER.name());
		}
		siteUserRepository.save(user);

		return "redirect:/login?register";
	}

	@RequestMapping("/spring_camp/problems")
	public String redirectProblem(@ModelAttribute("year") String year, @ModelAttribute("day") String day, @ModelAttribute("number") String number) {
		ArrayList<SpringCamp> list = (ArrayList<SpringCamp>) springCampRepository.findByYearAndDayAndNumber(year, Integer.parseInt(day), Integer.parseInt(number));
		return "redirect:/spring_camp/problems/" + list.get(0).getTitle().toLowerCase();
	}
	@GetMapping("/spring_camp/problems")
	public String redirectProblem2(@RequestParam("problemName") String problemName) {
		String newName = new String();
		for(int i = 0; i < problemName.length(); i++) {
			if(problemName.charAt(i) == ' ') newName += '_';
			else newName += problemName.charAt(i);
		}
		return "redirect:/spring_camp/problems/" + newName.toLowerCase();
	}
	@GetMapping("/spring_camp/problems/{problemName}")
	public String problems(@PathVariable("problemName") String problemName) {
		return "judge_system/spring_camp/problems/" + problemName;
	}

	@GetMapping("/result")
	public String result(@RequestParam("submissionId") String submissionId, Model model) throws IOException {
		String SourceCode = getSource(submissionId);
		model.addAttribute("SourceCode", SourceCode);
		ArrayList<SampleResult> list = (ArrayList<SampleResult>) sampleResultRepository.findBySubmissionNum(Long.parseLong(submissionId));
		model.addAttribute("resultList", list);
		return "judge_system/spring_camp/result";
	}

	@PostMapping("/result/{problem_snake_name}")
	public String exec_judge_scores(@ModelAttribute("user") SiteUser user, @ModelAttribute("submit") String SourceCode, Model model ,@PathVariable("problem_snake_name") String problemSnakeName) throws IOException, InterruptedException {
		String usr = SecurityContextHolder.getContext().getAuthentication().getName();
		long submissionId = totalResultRepository.count() + 1;
		TotalResult totalResult = new TotalResult();
		totalResult.setUser(usr);
		String newName = new String();
		for(int i = 0; i < problemSnakeName.length(); i++) {
			if(problemSnakeName.charAt(i) == '_') newName += ' ';
			else newName += problemSnakeName.charAt(i);
		}
		char[] arr = newName.toCharArray();
		arr[0] = Character.toUpperCase(arr[0]);
		String problemCamelName = new String(arr);
		totalResult.setProblemName(problemCamelName);

		ArrayList<SampleName> list = (ArrayList<SampleName>) sampleNameRepository.findByTitle(problemCamelName);
		generateSourceFile(SourceCode, submissionId);
		ArrayList<SampleResult> sampleResultList = getSampleResultList(list, testCasePathRepository.findByProblemName(problemCamelName).get(0).getTestCasePath(), submissionId, usr, problemCamelName);
		long resultId = sampleResultRepository.count() + 1;
		for(int i = 0; i < sampleResultList.size(); i++) {
			sampleResultList.get(i).setSampleResultId(resultId + i);
			sampleResultRepository.save(sampleResultList.get(i));
		}

		model.addAttribute("SourceCode", SourceCode);
		model.addAttribute("resultList", sampleResultList);
		if(sampleResultList.get(0).getSampleResult() == 5) {
			totalResult.setTotalResult(5);
			totalResultRepository.save(totalResult);
		} else {
			int result = 1;
			for(int i = 0; i < sampleResultList.size(); i++) {
				if(sampleResultList.get(i).getSampleResult() == 2 && (result == 1 || result > 2)) result = 2;
				else if(sampleResultList.get(i).getSampleResult() == 3 && (result == 1 || result > 3)) result = 3;
				else if(sampleResultList.get(i).getSampleResult() == 4 && result == 1) result = 4;
			}
			totalResult.setTotalResult(result);
			totalResultRepository.save(totalResult);
		}

		return "judge_system/spring_camp/result";
	}
}