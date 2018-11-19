package com.seezoon.admin.modules.demo.web;

import java.io.Serializable;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.github.pagehelper.PageInfo;
import com.seezoon.boot.common.web.BaseController;
import com.seezoon.boot.context.dto.ResponeModel;
import com.seezoon.service.modules.demo.entity.DemoGen;
import com.seezoon.service.modules.demo.service.DemoGenService;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.seezoon.service.modules.sys.entity.SysFile;
import com.seezoon.service.modules.sys.service.SysFileService;
import com.seezoon.boot.common.utils.FileUtils;
import com.seezoon.boot.common.file.handler.FileHandler;
import com.seezoon.service.modules.sys.dto.FileInfo;
import com.seezoon.boot.common.Constants;

/**
 * 生成案例controller
 * Copyright &copy; 2018 powered by huangdf, All rights reserved.
 * @author hdf 2018-11-19 22:43:25
 */
@RestController
@RequestMapping("${admin.path}/demo/gen")
public class DemoGenController extends BaseController {

	@Autowired
	private DemoGenService demoGenService;
	@Autowired
	private SysFileService sysFileService;
	@Autowired
	private FileHandler fileHandler;

	@PreAuthorize("hasAuthority('demo:gen:qry')")
	@PostMapping("/qryPage.do")
	public ResponeModel qryPage(DemoGen demoGen) {
		PageInfo<DemoGen> page = demoGenService.findByPage(demoGen, demoGen.getPage(), demoGen.getPageSize());
		return ResponeModel.ok(page);
	}
	@PreAuthorize("hasAuthority('demo:gen:qry')")
	@RequestMapping("/get.do")
	public ResponeModel get(@RequestParam String id) {
		DemoGen demoGen = demoGenService.findById(id);
        if (null != demoGen) {
		    if (StringUtils.isNotEmpty(demoGen.getRichText())) {
				demoGen.setRichText(StringEscapeUtils.unescapeHtml4(demoGen.getRichText()));
			}
		}
        	if (StringUtils.isNotEmpty(demoGen.getImage())) {
        		String[] images = StringUtils.split(demoGen.getImage(), Constants.SEPARATOR);
        		List<FileInfo> imageArray = new ArrayList<>();
	        	for (String path :images) {
	        		SysFile sysFile = sysFileService.findById(FileUtils.getFileId(path));
	        		if (null != sysFile) {
	        			imageArray.add(new FileInfo(fileHandler.getFullUrl(path),path,sysFile.getName()));
	        		}
	        	}
	        	demoGen.setImageArray(imageArray);
        }
        	if (StringUtils.isNotEmpty(demoGen.getFile())) {
        		String[] files = StringUtils.split(demoGen.getFile(), Constants.SEPARATOR);
        		List<FileInfo> fileArray = new ArrayList<>();
	        	for (String path :files) {
	        		SysFile sysFile = sysFileService.findById(FileUtils.getFileId(path));
	        		if (null != sysFile) {
	        			fileArray.add(new FileInfo(fileHandler.getFullUrl(path),path,sysFile.getName()));
	        		}
	        	}
	        	demoGen.setFileArray(fileArray);
        }
		return ResponeModel.ok(demoGen);
	}
	@PreAuthorize("hasAuthority('demo:gen:save')")
	@PostMapping("/save.do")
	public ResponeModel save(@Validated DemoGen demoGen, BindingResult bindingResult) {
		int cnt = demoGenService.save(demoGen);
		return ResponeModel.ok(cnt);
	}
	@PreAuthorize("hasAuthority('demo:gen:qry')")
	@PostMapping("/update.do")
	public ResponeModel update(@Validated DemoGen demoGen, BindingResult bindingResult) {
		int cnt = demoGenService.updateSelective(demoGen);
		return ResponeModel.ok(cnt);
	}
	@PreAuthorize("hasAuthority('demo:gen:delete')")
	@PostMapping("/delete.do")
	public ResponeModel delete(@RequestParam Serializable id) {
		int cnt = demoGenService.deleteById(id);
		return ResponeModel.ok(cnt);
	}
}
