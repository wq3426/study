package org.wq.spring.spring_mvc.web.controller;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {

	/**
	 * 1.使用MultipartFile接收上传的文件
	 * 2.使用FileUtils.writeByteArrayToFile()将文件写入到磁盘
	 * @param file
	 * @return
	 */
	@RequestMapping(value="upload", method=RequestMethod.POST)
	public @ResponseBody String uploadSingleFile(MultipartFile file){// 1
		
		try {
			// 2
			FileUtils.writeByteArrayToFile(new File("F:/upload/" + file.getOriginalFilename()), file.getBytes());
			return "success";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failure";
		}
	}
}
