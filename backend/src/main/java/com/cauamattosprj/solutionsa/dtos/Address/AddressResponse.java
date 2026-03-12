package com.cauamattosprj.solutionsa.dtos.Address;

import com.cauamattosprj.solutionsa.models.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class AddressResponse {

    private UUID id;
    private String cep;
    private String number;
    private String complement;
    private String street;
    private String neighborhood;
    private String city;
    private String state;
    private boolean main;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AddressResponse from(Address address) {
        AddressResponse response = new AddressResponse();
        response.id           = address.getId();
        response.cep          = address.getCep();
        response.number       = address.getNumber();
        response.complement   = address.getComplement();
        response.street       = address.getStreet();
        response.neighborhood = address.getNeighborhood();
        response.city         = address.getCity();
        response.state        = address.getState();
        response.main         = address.isMain();
        response.createdAt    = address.getCreatedAt();
        response.updatedAt    = address.getUpdatedAt();
        return response;
    }
}