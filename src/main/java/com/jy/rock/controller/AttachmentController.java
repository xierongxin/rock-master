package com.jy.rock.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.io.ByteStreams;
import com.jy.rock.bean.AttachmentSummaryVO;
import com.jy.rock.bean.UserWithGroupsVO;
import com.jy.rock.core.ParamEncryptUtil;
import com.jy.rock.dao.AttachmentDao;
import com.jy.rock.domain.Attachment;
import com.jy.rock.enums.AttachmentType;
import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author hzhou
 */
@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    private static final Cache<String, List<Attachment>> ATTACHMENT_CACHE = Caffeine.newBuilder()
            .maximumSize(2000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    @Autowired
    private AttachmentDao attachmentDao;

    @Nullable
    public static List<Attachment> getFormAttachments(@NotNull String formId) {
        return ATTACHMENT_CACHE.getIfPresent(formId);
    }

    private Attachment getTokenBasedAttachment(String id, String token, UserWithGroupsVO user) {
        String decrypt = ParamEncryptUtil.decrypt(user, token);
        if (!Objects.equals(decrypt, id)) {
            throw new IllegalArgumentException("token not valid");
        }

        return this.attachmentDao.selectByPrimaryKey(id);
    }

    /**
     * 上传单个附件
     *
     * @param file 附件
     * @return 附件ID
     * @throws IOException 读取附件错误
     */
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        Attachment attachment = new Attachment(file);
        attachmentDao.insert(attachment);
        return attachment.getId();
    }

    /**
     * 上传多个附件
     *
     * @param files 附件列表
     * @return 附件ID列表
     * @throws Exception 异常
     */
    @PostMapping("/upload_multi")
    public List<String> uploadFiles(@RequestParam("files") MultipartFile[] files) throws Exception {
        List<Attachment> attachments = new ArrayList<>(files.length);
        for (MultipartFile file : files) {
            attachments.add(new Attachment(file));
        }
        attachmentDao.insert(attachments);
        return attachments.stream().map(BaseDomainWithGuidKey::getId).collect(Collectors.toList());
    }

    @PostMapping("/upload_to_cache")
    public void uploadFileToCache(@RequestParam("formId") String formId, @RequestParam("file") MultipartFile file) throws IOException {
        Attachment attachment = new Attachment(file);

        ATTACHMENT_CACHE.getIfPresent(formId);
        List<Attachment> attachments = ATTACHMENT_CACHE.getIfPresent(formId);
        if (attachments == null) {
            ATTACHMENT_CACHE.put(formId, new ArrayList<Attachment>() {{
                add(attachment);
            }});
        } else {
            attachments.add(attachment);
        }
    }

    @GetMapping(path = {
            "/recorder_attachments/{recorderId}",
            "/recorder_attachments/{recorderId}/{attachmentType}"
    })
    public List<AttachmentSummaryVO> getAttachments(@PathVariable String recorderId, @PathVariable(required = false) AttachmentType attachmentType,
                                                    @AuthenticationPrincipal UserWithGroupsVO user) throws Exception {
        List<AttachmentSummaryVO> result = attachmentDao.getAttachmentsByRecorderIdAndAttachmentType(recorderId, attachmentType);
        for (AttachmentSummaryVO summaryVO : result) {
            summaryVO.setToken(ParamEncryptUtil.encrypt(user, summaryVO.getId()));
        }
        return result;
    }

    @GetMapping("/download/{id}/{token}")
    public void download(@PathVariable String id, @PathVariable String token, @AuthenticationPrincipal UserWithGroupsVO user, HttpServletResponse response) throws IOException {
        Attachment attachment = getTokenBasedAttachment(id, token, user);
        if (attachment == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.flushBuffer();
        } else {
            // 解决下载文件名乱码的问题
            String fileName = new String(attachment.getName().getBytes("gbk"), "iso8859-1");
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", MessageFormat.format("attachment;filename=\"{0}\"", fileName));
            response.addHeader("Program", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            ServletOutputStream outputStream = response.getOutputStream();
            ByteStreams.copy(new ByteArrayInputStream(attachment.getContent()), outputStream);

            response.flushBuffer();
        }
    }

    @GetMapping("/preview_image/{id}/{token}")
    public byte[] previewImage(@PathVariable String id, @PathVariable String token, @AuthenticationPrincipal UserWithGroupsVO user, HttpServletResponse response) throws IOException {
        Attachment attachment = getTokenBasedAttachment(id, token, user);
        if (attachment == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.flushBuffer();
            return null;
        } else {
            return attachment.getContent();
        }
    }
}
