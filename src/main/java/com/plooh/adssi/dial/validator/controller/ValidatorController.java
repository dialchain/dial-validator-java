package com.plooh.adssi.dial.validator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ValidatorController {

    /**
     * Returns a file containinng the latest version of the decclaration with the
     * given id
     * 
     * @return
     */
    @GetMapping("/declaration/{id}")
    public String getDeclaration() {
        return null;
    }

    /**
     * Returns a signed actual list of all validators active in the current time
     * window.
     * 
     * @return
     */
    @GetMapping("/validators")
    public String getValidators() {
        return null;
    }

    /**
     * Returns a signed actual list of all known to the current time window.
     * 
     * @return
     */
    @GetMapping("/routers")
    public String getRouters() {
        return null;
    }

    /**
     * Validates and published the list of declarations contained in the current
     * file.
     * 
     * @param record
     * @return
     */
    @PostMapping("/publish")
    public String publishDeclarations(@RequestBody String record) {
        return null;
    }

    /**
     * Analyzes and returns the price required to validate and publish the current
     * record.
     * 
     * @param record
     * @return
     */
    @PostMapping("/price")
    public String priceDeclarations(@RequestBody String record) {
        return null;
    }
}
