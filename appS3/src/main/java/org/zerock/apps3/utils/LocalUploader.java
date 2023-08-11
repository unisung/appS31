package org.zerock.apps3.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;

@Component
@Log4j2
public class LocalUploader {
	@Value("${org.zerock.upload.path}")
	private String uploadPath;
	
	public List<String> uploadLocal(MultipartFile multipartFile){
		if(multipartFile == null || multipartFile.isEmpty()) return null;
		
		String uuid = UUID.randomUUID().toString();//파일아이디 중복 방지처리
		String saveFileName = uuid + "_" + multipartFile.getOriginalFilename();
		Path savePath = Paths.get(uploadPath,saveFileName);
		List<String> savePathList = new ArrayList<>();
		
		try {
			  multipartFile.transferTo(savePath);//업로드 처리
			  
			  savePathList.add(savePath.toFile().getAbsolutePath());//절대경로
			  //이미지파일인 경유 썸네일 생성
			  if(Files.probeContentType(savePath).startsWith("image")) {
				  File thumbFile = new File(uploadPath, "s_"+saveFileName);//s_파일명
				  savePathList.add(thumbFile.getAbsolutePath());
				  //200x200 썸네일 생성
				  Thumbnailator.createThumbnail(savePath.toFile(), thumbFile,200,200);
			  }
		} catch (Exception e) {
			log.error("ERROR: "+e.getMessage());
			e.printStackTrace();
		}
		return savePathList;
	}
}
