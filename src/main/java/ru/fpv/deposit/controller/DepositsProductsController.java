package ru.fpv.deposit.controller;


import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fpv.deposit.dto.CreateDefaultProductResponse;

import ru.fpv.deposit.dto.DepositProductResponse;

import ru.fpv.deposit.service.DepositsProductsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/fvp/dep")
@RequiredArgsConstructor
public class DepositsProductsController {

    private final DepositsProductsService depositsProductsService;

    @PostMapping("/deposit-product")
    public ResponseEntity<CreateDefaultProductResponse> createDefaultProducts() {
        CreateDefaultProductResponse response =
                depositsProductsService.createDefaultProductResponse();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deposit-product")
    public ResponseEntity<List<DepositProductResponse>> getDepositProducts(
            @RequestParam(required = false) Boolean isExpired,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime modifiedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime modifiedTo
    ) {
        return ResponseEntity.ok(
                depositsProductsService.getDepositProducts(isExpired, createdFrom, createdTo, modifiedFrom, modifiedTo)
        );
    }

    @GetMapping("/deposit-product/{id}")
    public ResponseEntity<DepositProductResponse> getDepositProductById(@PathVariable Long id) {
        return ResponseEntity.ok(depositsProductsService.getDepositProductById(id));
    }

}