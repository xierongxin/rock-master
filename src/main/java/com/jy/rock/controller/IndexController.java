package com.jy.rock.controller;

import com.google.common.base.Strings;
import com.jy.rock.bean.CodeImageVO;
import com.jy.rock.bean.ResetMyPasswordVO;
import com.jy.rock.bean.UserWithGroupsVO;
import com.jy.rock.core.WebSettings;
import com.jy.rock.core.audit.AuditMethod;
import com.jy.rock.security.MyAuthenticationFilter;
import com.jy.rock.service.UserServiceImpl;
import com.jy.rock.utils.CodeUtil;
import com.xmgsd.lan.roadhog.bean.SimpleResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static com.jy.rock.core.SystemConfig.VALID_CODE_ATTRIBUTE;

/**
 * @author hzhou
 */
@RestController
@RequestMapping("")
public class IndexController {

    @Autowired
    public HttpSession session;

    @Autowired
    public WebSettings webSettings;

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/web_settings")
    public WebSettings webSettings() {
        return this.webSettings;
    }

    @GetMapping("/current_user")
    public UserWithGroupsVO getCurrentUser(@AuthenticationPrincipal UserWithGroupsVO user) {
        return user;
    }

    @GetMapping(value = "/code_image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] codeImage() throws IOException {
        CodeImageVO codeImageVO = CodeUtil.generateCodeAndPic();
        session.setAttribute(VALID_CODE_ATTRIBUTE, codeImageVO.getCode());
        BufferedImage img = codeImageVO.getImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        return baos.toByteArray();
    }

    @GetMapping("/unique_id")
    public String getUniqueId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @PutMapping("/reset_my_password")
    @AuditMethod(name = "修改密码")
    public SimpleResponseVO resetMyPassword(@Valid @RequestBody ResetMyPasswordVO resetMyPasswordVO, HttpServletRequest request) {
        String code = MyAuthenticationFilter.getSessionValidCode(request);
        if (Strings.isNullOrEmpty(code) || !code.toLowerCase().equals(resetMyPasswordVO.getCode().toLowerCase())) {
            return new SimpleResponseVO(false, "valid code error", HttpStatus.BAD_REQUEST.value());
        }
        this.userService.resetMyPassword(resetMyPasswordVO);
        return new SimpleResponseVO(true);
    }
}
