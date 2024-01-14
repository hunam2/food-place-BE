package com.github.accountmanagementproject.web.controller.authAccount;

import com.github.accountmanagementproject.repository.userDetails.CustomUserDetails;
import com.github.accountmanagementproject.service.authAccount.AccountService;
import com.github.accountmanagementproject.web.dto.account.AccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/my-page")
    public AccountDto getMyInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return accountService.getMyInfo(customUserDetails);
    }

    @PatchMapping("/my-page")
    public AccountDto patchMyInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody AccountDto accountDTO){
        return accountService.patchMyInfo(customUserDetails, accountDTO);
    }

//    @PostMapping("/my-page/cart")
//    public String cartAddItem(HttpServletRequest httpServletRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails){
//        return accountService.cartAddItem(httpServletRequest.getParameter("option-id"),
//                httpServletRequest.getParameter("quantity"),
//                customUserDetails);
//    }
    @PostMapping("/withdrawal")
    public String withdrawal(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return accountService.withdrawal(customUserDetails);
    }

}
