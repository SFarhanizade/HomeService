package ir.farhanizade.homeservice.controller;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bank")
public class BankController {

    //@PostMapping("/pay")
    public boolean pay() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(111, 36);
        while(userAnswersCaptcha(captcha)==false);
        return true;
    }

    private boolean userAnswersCaptcha(AbstractCaptcha captcha) {
        String code = captcha.getCode();
        return code == captcha.getCode();
    }
}
