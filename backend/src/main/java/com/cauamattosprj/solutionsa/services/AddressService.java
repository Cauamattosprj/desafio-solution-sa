package com.cauamattosprj.solutionsa.services;

import com.cauamattosprj.solutionsa.dtos.address.AddressRequest;
import com.cauamattosprj.solutionsa.dtos.address.AddressResponse;
import com.cauamattosprj.solutionsa.models.Address;
import com.cauamattosprj.solutionsa.models.User;
import com.cauamattosprj.solutionsa.repositories.AddressRepository;
import com.cauamattosprj.solutionsa.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<AddressResponse> findAllByUser(UUID userId) {
        verifyAccess(userId);
        return addressRepository.findAllByUserId(userId)
                .stream()
                .map(AddressResponse::from)
                .toList();
    }

    public AddressResponse findById(UUID id, UUID userId) {
        verifyAccess(userId);
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado"));
        return AddressResponse.from(address);
    }

    @Transactional
    public AddressResponse create(UUID userId, AddressRequest request) {
        verifyAccess(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (request.isMain()) {
            addressRepository.unsetMainByUserId(userId, UUID.randomUUID()); // nenhum a excluir ainda
        }

        Address address = Address.builder()
                .user(user)
                .cep(request.getCep())
                .number(request.getNumber())
                .complement(request.getComplement())
                .street(request.getStreet())
                .neighborhood(request.getNeighborhood())
                .city(request.getCity())
                .state(request.getState())
                .main(request.isMain())
                .build();

        return AddressResponse.from(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse update(UUID id, UUID userId, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado"));

        if (request.isMain()) {
            addressRepository.unsetMainByUserId(userId, id);
        }

        address.setCep(request.getCep());
        address.setNumber(request.getNumber());
        address.setComplement(request.getComplement());
        address.setStreet(request.getStreet());
        address.setNeighborhood(request.getNeighborhood());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setMain(request.isMain());

        return AddressResponse.from(addressRepository.save(address));
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        verifyAccess(userId);
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado"));

        boolean wasMain = Boolean.TRUE.equals(address.isMain());
        addressRepository.deleteById(id);

        if (wasMain) {
            addressRepository.findAllByUserId(userId)
                    .stream()
                    .findFirst()
                    .ifPresent(next -> {
                        next.setMain(true);
                        addressRepository.save(next);
                    });
        }
    }

    private void verifyAccess(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof com.cauamattosprj.solutionsa.security.UserDetailsImpl userDetails)) {
            throw new AccessDeniedException("Acesso negado");
        }

        User currentUser = userDetails.getUser();
        if (currentUser.getRole() == User.Role.ADMIN) {
            return;
        }
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("Acesso negado");
        }
    }
}