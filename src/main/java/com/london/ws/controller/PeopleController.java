package com.london.ws.controller;

import com.london.common.ApplicationException;
import com.london.dto.People;
import com.london.service.SearchService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Slf4j
@Validated
@RestController
public class PeopleController {
    static final String DEFAULT_CITY = "London";
    static final String DEFAULT_MILES = "50";

    @Autowired
    private SearchService searchService;

    @ApiIgnore
    @RequestMapping(value = "/")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }

    @ApiOperation(
        value = "People who are listed as either living in given city, or whose current coordinates are within given miles of the city",
        notes = "With default values this endpoint returns people who are listed as either living in London, or whose current coordinates are within 50 miles of London",
        response = People.class,
        responseContainer = "List")
    @RequestMapping( value = "/people",
        produces = APPLICATION_JSON_VALUE,
        method = GET)
    public ResponseEntity<List<People>> listPeople(@RequestParam(required = false, defaultValue = DEFAULT_CITY) String city,
                                                       @RequestParam(required = false, defaultValue = DEFAULT_MILES)
                                                       @Min(value = 0L, message = "{request.miles.format}") Double miles)
        throws ApplicationException {

        List<People> results = searchService.searchPeopleWithinRadius(city, miles);

        if (results != null) {
            log.info("Number of people in city of {} and within {} miles are {}", city, miles, results.size());
            return new ResponseEntity<>(results, HttpStatus.OK);
        }
        return ResponseEntity.ok(new ArrayList<>());
    }

}
