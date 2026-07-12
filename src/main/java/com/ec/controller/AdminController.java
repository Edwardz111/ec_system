package com.ec.controller;

import com.ec.pojo.*;
import com.ec.pojo.vo.AdminDashboardVo;
import com.ec.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    private boolean notLoggedIn(HttpSession session) {
        return session.getAttribute("ADMIN_INFO") == null;
    }

    @GetMapping("/admin/login")
    public String showLoginPage() {
        return "admin/admin_login";
    }

    @PostMapping("/admin/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        Admin admin = adminService.login(username, password);
        if (admin != null) {
            session.setAttribute("ADMIN_INFO", admin);
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("error", "账号或密码错误，或该用户不是管理员");
        return "admin/admin_login";
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("ADMIN_INFO");
        return "redirect:/admin/login";
    }

    @GetMapping("/admin/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        model.addAttribute("stats", adminService.getDashboardStats());
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String showUserManage(@RequestParam(required = false) String username,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(required = false) Integer vip,
                                 Model model,
                                 HttpSession session) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        model.addAttribute("userList", adminService.searchUsers(username, status, vip));
        return "admin/user_manage";
    }

    @GetMapping("/admin/users/operate")
    public String showUserOperate(@RequestParam Long id, HttpSession session, Model model) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        User user = adminService.getUserById(id);
        if (user == null) return "redirect:/admin/users";
        Admin operator = (Admin) session.getAttribute("ADMIN_INFO");
        Admin adminRecord = adminService.getAdminRecord(id);
        model.addAttribute("user", user);
        model.addAttribute("adminRecord", adminRecord);
        model.addAttribute("operatorSuperAdmin", adminService.isSuperAdmin(operator));
        model.addAttribute("isSelf", operator != null && id.equals(operator.getUserId()));
        return "admin/user_operate";
    }

    @PostMapping("/admin/users/cancel-vip")
    public String cancelUserVip(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        boolean ok = adminService.cancelVip(id);
        redirectAttributes.addFlashAttribute("message", ok ? "已取消该用户的 VIP" : "操作失败");
        return "redirect:/admin/users/operate?id=" + id;
    }

    @PostMapping("/admin/users/set-vip")
    public String setUserVip(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        boolean ok = adminService.setVip(id);
        redirectAttributes.addFlashAttribute("message", ok ? "已将该用户设为 VIP" : "操作失败");
        return "redirect:/admin/users/operate?id=" + id;
    }

    @PostMapping("/admin/users/ban")
    public String banUser(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        boolean ok = adminService.banUser(id);
        redirectAttributes.addFlashAttribute("message", ok ? "用户已封禁" : "操作失败");
        return "redirect:/admin/users/operate?id=" + id;
    }

    @PostMapping("/admin/users/grant-admin")
    public String grantAdmin(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        Admin operator = (Admin) session.getAttribute("ADMIN_INFO");
        String err = adminService.grantAdminRole(id, "ADMIN", operator);
        redirectAttributes.addFlashAttribute("message", err == null ? "已设为管理员" : err);
        return "redirect:/admin/users/operate?id=" + id;
    }

    @PostMapping("/admin/users/grant-super-admin")
    public String grantSuperAdmin(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        Admin operator = (Admin) session.getAttribute("ADMIN_INFO");
        String err = adminService.grantAdminRole(id, "SUPER_ADMIN", operator);
        redirectAttributes.addFlashAttribute("message", err == null ? "已设为超级管理员" : err);
        return "redirect:/admin/users/operate?id=" + id;
    }

    @PostMapping("/admin/users/revoke-admin")
    public String revokeAdmin(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        Admin operator = (Admin) session.getAttribute("ADMIN_INFO");
        String err = adminService.revokeAdminRole(id, operator);
        redirectAttributes.addFlashAttribute("message", err == null ? "已解除管理员权限" : err);
        return "redirect:/admin/users/operate?id=" + id;
    }

    /** 商品完整管理（分类/商品/活动）→ 原 product 模块 product_admin.html */
    @GetMapping("/admin/products")
    public String showProductManage(HttpSession session) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        return "redirect:/product_admin.html";
    }

    @GetMapping("/admin/orders")
    public String showOrderManage(HttpSession session, Model model) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        AdminDashboardVo stats = adminService.getDashboardStats();
        model.addAttribute("orderList", adminService.listOrders());
        model.addAttribute("orderTotal", stats.getOrderCount());
        model.addAttribute("paidOrderCount", stats.getPaidOrderCount());
        model.addAttribute("pendingShipCount", stats.getPendingShipCount());
        return "admin/order_manage";
    }

    @GetMapping("/admin/orders/detail")
    public String showOrderDetail(@RequestParam Long id, HttpSession session, Model model) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        Order order = adminService.getOrderDetail(id);
        if (order == null) return "redirect:/admin/orders";
        model.addAttribute("order", order);
        model.addAttribute("orderItems", adminService.getOrderItemsWithProductName(id));
        return "admin/order_info";
    }

    @GetMapping("/admin/reviews")
    public String showReviewManage(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String rating,
            @RequestParam(required = false) String status,
            HttpSession session, Model model) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        Long uid = AdminService.parseIdParam(userId);
        Long oid = AdminService.parseIdParam(orderId);
        Long pid = AdminService.parseIdParam(productId);
        Integer ratingVal = AdminService.parseRatingParam(rating);
        String statusVal = (status != null && !status.trim().isEmpty()) ? status.trim() : null;

        AdminDashboardVo stats = adminService.getDashboardStats();
        model.addAttribute("reviewList", adminService.searchReviews(uid, oid, pid, ratingVal, statusVal));
        model.addAttribute("reviewTotal", stats.getReviewCount());
        model.addAttribute("goodReviewCount", stats.getGoodReviewCount());
        model.addAttribute("pendingReplyCount", stats.getPendingReplyCount());
        model.addAttribute("filterUserId", userId != null ? userId : "");
        model.addAttribute("filterOrderId", orderId != null ? orderId : "");
        model.addAttribute("filterProductId", productId != null ? productId : "");
        model.addAttribute("filterRating", rating != null ? rating : "");
        model.addAttribute("filterStatus", statusVal != null ? statusVal : "");
        return "admin/review_manage";
    }

    @PostMapping("/admin/reviews/reply")
    public String replyReview(@RequestParam Long id,
                              @RequestParam String reply,
                              @RequestParam(required = false) String userId,
                              @RequestParam(required = false) String orderId,
                              @RequestParam(required = false) String productId,
                              @RequestParam(required = false) String rating,
                              @RequestParam(required = false) String status,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        String err = adminService.replyReview(id, reply);
        redirectAttributes.addFlashAttribute("message", err == null ? "回复成功" : err);
        return "redirect:/admin/reviews" + reviewSearchSuffix(userId, orderId, productId, rating, status);
    }

    @PostMapping("/admin/reviews/toggle-visibility")
    public String toggleReviewVisibility(@RequestParam Long id,
                                         @RequestParam(required = false) String userId,
                                         @RequestParam(required = false) String orderId,
                                         @RequestParam(required = false) String productId,
                                         @RequestParam(required = false) String rating,
                                         @RequestParam(required = false) String status,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        String err = adminService.toggleReviewVisibility(id);
        redirectAttributes.addFlashAttribute("message", err == null ? "状态已更新" : err);
        return "redirect:/admin/reviews" + reviewSearchSuffix(userId, orderId, productId, rating, status);
    }

    @PostMapping("/admin/reviews/delete")
    public String deleteReview(@RequestParam Long id,
                               @RequestParam(required = false) String userId,
                               @RequestParam(required = false) String orderId,
                               @RequestParam(required = false) String productId,
                               @RequestParam(required = false) String rating,
                               @RequestParam(required = false) String status,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (notLoggedIn(session)) return "redirect:/admin/login";
        String err = adminService.deleteReviewPermanently(id);
        redirectAttributes.addFlashAttribute("message", err == null ? "评价已删除" : err);
        return "redirect:/admin/reviews" + reviewSearchSuffix(userId, orderId, productId, rating, status);
    }

    private String reviewSearchSuffix(String userId, String orderId, String productId,
                                      String rating, String status) {
        return AdminService.buildReviewSearchQuery(
                AdminService.parseIdParam(userId),
                AdminService.parseIdParam(orderId),
                AdminService.parseIdParam(productId),
                AdminService.parseRatingParam(rating),
                status != null && !status.trim().isEmpty() ? status.trim() : null);
    }
}
