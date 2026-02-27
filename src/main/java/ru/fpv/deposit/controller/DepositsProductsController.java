package ru.fpv.deposit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fpv.deposit.dto.CreateDefaultProductResponse;
import ru.fpv.deposit.dto.UpdateDepositProductRequest;
import ru.fpv.deposit.model.DepositProduct;
import ru.fpv.deposit.repository.DepositsProductsRepository;
import ru.fpv.deposit.service.DepositsProductsService;
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

    @PatchMapping("/deposit-product")
    public ResponseEntity<DepositProduct> updateDepositProduct(
            @Valid @RequestBody UpdateDepositProductRequest request) {

        DepositProduct updatedProduct = depositsProductsService.updateDepositProduct(request);
        return ResponseEntity.ok(updatedProduct);
    }
}