package com.ec.service.impl;

import com.ec.mapper.AddressMapper;
import com.ec.mapper.EmailverifycodeMapper;
import com.ec.mapper.UserMapper;
import com.ec.pojo.Address;
import com.ec.pojo.User;
import com.ec.pojo.dto.Logindto;
import com.ec.pojo.dto.Registerdto;
import com.ec.pojo.vo.Uservo;
import com.ec.service.Userservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class Userserviceimpl implements Userservice {

    @Autowired private UserMapper userMapper;
    @Autowired private AddressMapper addressMapper;
    @Autowired private EmailverifycodeMapper codeMapper;
    @Autowired private JavaMailSender mailSender;

    @Value("${wechat.appid:YOUR_APPID}")
    private String wechatAppid;

    @Value("${wechat.secret:YOUR_SECRET}")
    private String wechatSecret;

    @Value("${wechat.redirect-uri:http://localhost:8080/user/wechat/callback}")
    private String wechatRedirectUri;

    @Value("${spring.mail.username:noreply@example.com}")
    private String mailFrom;

    // ═══════════════════════════════════════════════════════
    //  注册 / 登录
    // ═══════════════════════════════════════════════════════

    @Transactional
    @Override
    public void register(Registerdto dto) {
        String savedCode = codeMapper.selectLatestCode(dto.getEmail(), "REGISTER");
        if (savedCode == null || !savedCode.equals(dto.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }
        if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.selectByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());   // 明文存储，不加密
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("USER");
        user.setIsVip(0);
        user.setStatus("ACTIVE");
        userMapper.insert(user);

        codeMapper.markUsed(dto.getEmail(), dto.getCode(), "REGISTER");
    }

    @Override
    public Uservo loginByPassword(Logindto dto) {
        User user = userMapper.selectByUsername(dto.getAccount());
        if (user == null) {
            user = userMapper.selectByEmail(dto.getAccount());
        }
        if (user == null) throw new RuntimeException("账号不存在");
        if (isDisabled(user)) throw new RuntimeException("账号已被禁用，请联系客服");

        // 明文直接比较，不用 BCrypt
        if (!dto.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return Uservo.of(user);
    }

    @Override
    public Uservo loginByEmailCode(String email, String code) {
        String saved = codeMapper.selectLatestCode(email, "LOGIN");
        if (saved == null || !saved.equals(code)) throw new RuntimeException("验证码错误或已过期");
        User user = userMapper.selectByEmail(email);
        if (user == null) throw new RuntimeException("该邮箱未注册");
        if (isDisabled(user)) throw new RuntimeException("账号已被禁用");
        codeMapper.markUsed(email, code, "LOGIN");
        return Uservo.of(user);
    }

    @Override
    public void sendEmailCode(String email, String type) {
        String code = String.format("%06d", new Random().nextInt(1_000_000));
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(5);
        codeMapper.insert(email, code, type, expireTime);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mailFrom);
        msg.setTo(email);
        msg.setSubject("【电商系统】邮箱验证码");
        msg.setText("您的验证码为：" + code + "，5分钟内有效，请勿泄露给他人。");
        mailSender.send(msg);
    }

    // ═══════════════════════════════════════════════════════
    //  微信登录
    //
    //  说明：微信开放平台（open.weixin.qq.com）的 snsapi_login
    //  需要已认证的企业/组织主体才能使用，个人主体会报 Scope 错误。
    //
    //  改用公众号网页授权（mp.weixin.qq.com）的 snsapi_userinfo，
    //  个人订阅号/服务号均可使用，只需在公众号后台配置网页授权域名。
    // ═══════════════════════════════════════════════════════

    @Override
    public Map<String, String> getWechatQrInfo() {
        String state = UUID.randomUUID().toString().replace("-", "");

        // 公众号网页授权 URL（snsapi_userinfo 可获取头像昵称；snsapi_base 只获取 openid）
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize"
                + "?appid=" + wechatAppid
                + "&redirect_uri=" + wechatRedirectUri
                + "&response_type=code"
                + "&scope=snsapi_userinfo"      // ← 改为 snsapi_userinfo，个人号可用
                + "&state=" + state
                + "#wechat_redirect";

        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("state", state);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> handleWechatCallback(String code, String state) {
        Map<String, Object> result = new HashMap<>();

        // 1. code 换 access_token + openid（公众号接口地址）
        String tokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token"
                + "?appid=" + wechatAppid
                + "&secret=" + wechatSecret
                + "&code=" + code
                + "&grant_type=authorization_code";

        RestTemplate restTemplate = new RestTemplate();
        @SuppressWarnings("unchecked")
        Map<String, Object> tokenResp = restTemplate.getForObject(tokenUrl, Map.class);

        if (tokenResp == null || tokenResp.containsKey("errcode")) {
            throw new RuntimeException("微信授权失败：" +
                    (tokenResp != null ? tokenResp.get("errmsg") : "无响应"));
        }

        String openid      = (String) tokenResp.get("openid");
        String accessToken = (String) tokenResp.get("access_token");

        // 2. 查找是否已绑定
        User user = userMapper.selectByWechatOpenid(openid);
        if (user != null) {
            result.put("status", "login");
            result.put("userVO", Uservo.of(user));
        } else {
            result.put("status", "bind");
            result.put("openid", openid);

            // 获取微信用户信息（昵称、头像）
            String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo"
                    + "?access_token=" + accessToken
                    + "&openid=" + openid
                    + "&lang=zh_CN";
            @SuppressWarnings("unchecked")
            Map<String, Object> wxUser = restTemplate.getForObject(userInfoUrl, Map.class);
            if (wxUser != null) {
                result.put("nickname", wxUser.get("nickname"));
                result.put("avatar",   wxUser.get("headimgurl"));
            }
        }
        return result;
    }

    @Override
    public void bindWechat(Long userId, String openid) {
        User exist = userMapper.selectByWechatOpenid(openid);
        if (exist != null && !exist.getId().equals(userId)) {
            throw new RuntimeException("该微信已绑定其他账号");
        }
        userMapper.bindWechat(userId, openid);
    }

    // ═══════════════════════════════════════════════════════
    //  个人信息
    // ═══════════════════════════════════════════════════════

    @Override
    public Uservo getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new RuntimeException("用户不存在");
        return Uservo.of(user);
    }

    @Override
    public void updateInfo(User user) {
        userMapper.updateInfo(user);
    }

    @Override
    public void updatePassword(Long userId, String oldPwd, String newPwd) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new RuntimeException("用户不存在");
        if (!oldPwd.equals(user.getPassword())) throw new RuntimeException("旧密码不正确");
        userMapper.updatePassword(userId, newPwd);
    }

    // ═══════════════════════════════════════════════════════
    //  收货地址
    // ═══════════════════════════════════════════════════════

    @Override
    public List<Address> listAddress(Long userId) {
        return addressMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public void addAddress(Address address) {
        List<Address> existing = addressMapper.selectByUserId(address.getUserId());
        if (existing.isEmpty()) address.setIsDefault(1);
        addressMapper.insert(address);
    }

    @Override
    public void updateAddress(Address address) {
        addressMapper.update(address);
    }

    @Override
    public void deleteAddress(Long addressId, Long userId) {
        addressMapper.delete(addressId, userId);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long addressId, Long userId) {
        addressMapper.clearDefault(userId);
        addressMapper.setDefault(addressId);
    }

    @Override
    public Address getDefaultAddress(Long userId) {
        List<Address> list = addressMapper.selectByUserId(userId);
        return list.stream()
                .filter(a -> a.getIsDefault() != null && a.getIsDefault() == 1)
                .findFirst()
                .orElse(list.isEmpty() ? null : list.get(0));
    }

    @Override
    public Address getAddressForUser(Long addressId, Long userId) {
        if (addressId == null || userId == null) {
            return null;
        }
        Address address = addressMapper.selectById(addressId);
        if (address == null || !userId.equals(address.getUserId())) {
            return null;
        }
        return address;
    }

    @Override
    public boolean existsUser(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null && !isDisabled(user);
    }

    private boolean isDisabled(User user) {
        String status = user.getStatus();
        return "DISABLED".equalsIgnoreCase(status)
                || "BANNED".equalsIgnoreCase(status)
                || "0".equals(status);
    }

    @Override
    public String getUsernameById(Long userId) {
        User user = userMapper.selectById(userId);
        return user == null ? "未知用户" : user.getUsername();
    }

    @Override
    public User findUserEntityById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }
}