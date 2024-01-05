package mk.ukim.finki.wp.june2022.g1.service.impl;

import mk.ukim.finki.wp.june2022.g1.model.OSType;
import mk.ukim.finki.wp.june2022.g1.model.VirtualServer;
import mk.ukim.finki.wp.june2022.g1.model.exceptions.InvalidVirtualMachineIdException;
import mk.ukim.finki.wp.june2022.g1.repository.UserRepository;
import mk.ukim.finki.wp.june2022.g1.repository.VirtualServerRepository;
import mk.ukim.finki.wp.june2022.g1.service.VirtualServerService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VirtualServerServiceImpl implements VirtualServerService {
    private final VirtualServerRepository virtualServerRepository;
    private final UserRepository userRepository;

    public VirtualServerServiceImpl(VirtualServerRepository virtualServerRepository, UserRepository userRepository) {
        this.virtualServerRepository = virtualServerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<VirtualServer> listAll() {
        return virtualServerRepository.findAll();
    }

    @Override
    public VirtualServer findById(Long id) {
        return virtualServerRepository.findById(id).orElseGet(null);
    }

    @Override
    public VirtualServer create(String name, String ipAddress, OSType osType, List<Long> owners, LocalDate launchDate) {
        return virtualServerRepository.save(new VirtualServer(
                name,
                ipAddress,
                osType,
                userRepository.findAllById(owners),
                launchDate
        ));
    }

    @Override
    public VirtualServer update(Long id, String name, String ipAddress, OSType osType, List<Long> owners) {
        Optional<VirtualServer> virtualServer = virtualServerRepository.findById(id);

        if (!virtualServer.isPresent()) {
            throw new InvalidVirtualMachineIdException();
        }

        virtualServer.get().setId(id);
        virtualServer.get().setInstanceName(name);
        virtualServer.get().setIpAddress(ipAddress);
        virtualServer.get().setOSType(osType);
        virtualServer.get().setOwners(userRepository.findAllById(owners));

        return virtualServerRepository.save(virtualServer.get());
    }

    @Override
    public VirtualServer delete(Long id) {
        Optional<VirtualServer> virtualServer = virtualServerRepository.findById(id);

        if (virtualServer.isPresent()) {
            virtualServerRepository.delete(virtualServer.get());
            return virtualServer.get();
        }

        throw new InvalidVirtualMachineIdException();
    }

    @Override
    public VirtualServer markTerminated(Long id) {
        Optional<VirtualServer> optionalVirtualServer = virtualServerRepository.findById(id);

        if (optionalVirtualServer.isPresent()) {
            optionalVirtualServer.get().setTerminated(true);
            System.out.println(optionalVirtualServer.get().getTerminated());
            return virtualServerRepository.save(optionalVirtualServer.get());
        }

        throw new InvalidVirtualMachineIdException();
    }

    //todo
    @Override
    public List<VirtualServer> filter(Long ownerId, Integer activeMoreThanDays) {
        if (ownerId != null && activeMoreThanDays != null) {
            return virtualServerRepository.findAllByLaunchDateBeforeAndOwnersContains(LocalDate.now().minusDays(activeMoreThanDays), userRepository.getById(ownerId));
        } else if (ownerId != null) {
            return virtualServerRepository.findAllByOwnersContains(userRepository.getById(ownerId));
        } else if (activeMoreThanDays != null) {
            return virtualServerRepository.findAllByLaunchDateBefore(LocalDate.now().minusDays(activeMoreThanDays));
        } else {
            return virtualServerRepository.findAll();
        }
    }
}
