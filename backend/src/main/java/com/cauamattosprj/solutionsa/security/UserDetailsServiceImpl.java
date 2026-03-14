package com.cauamattosprj.solutionsa.security;

import com.cauamattosprj.solutionsa.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        return userRepository.findByCpf(cpf)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}