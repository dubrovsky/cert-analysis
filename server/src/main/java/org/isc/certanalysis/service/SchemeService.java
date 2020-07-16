package org.isc.certanalysis.service;

import org.isc.certanalysis.domain.CrlUrl;
import org.isc.certanalysis.domain.NotificationGroup;
import org.isc.certanalysis.domain.Scheme;
import org.isc.certanalysis.repository.NotificationGroupRepository;
import org.isc.certanalysis.repository.SchemeRepository;
import org.isc.certanalysis.service.bean.dto.SchemeDTO;
import org.isc.certanalysis.service.mapper.Mapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class SchemeService {

    private final SchemeRepository schemeRepository;
    private final NotificationGroupRepository notificationGroupRepository;
    private final FileService fileService;
    private final Mapper mapper;

    public SchemeService(SchemeRepository schemeRepository, NotificationGroupRepository notificationGroupRepository, FileService fileService, Mapper mapper) {
        this.schemeRepository = schemeRepository;
        this.notificationGroupRepository = notificationGroupRepository;
        this.fileService = fileService;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<SchemeDTO> findAll() {
        List<Scheme> schemes = schemeRepository.findAll(Sort.by("sort"));
        List<SchemeDTO> schemesDTO = new ArrayList<>(schemes.size());
        schemes.forEach(scheme -> {
            SchemeDTO schemeDTO = mapper.map(scheme, SchemeDTO.class);
            schemeDTO.setCertificates(fileService.filesToCertificates(scheme.getFiles(), "name", 0));
            schemesDTO.add(schemeDTO);
        });

        return schemesDTO;
    }

    public SchemeDTO create(SchemeDTO schemeDTO) {
        Scheme newScheme = mapper.map(schemeDTO, Scheme.class);
        for (Long id : schemeDTO.getNotificationGroupIds()) {
            newScheme.addNotificationGroup(notificationGroupRepository.getOne(id));
        }
        schemeDTO.getCrlUrls().forEach(crlUrlDTO -> newScheme.addCrlUrl(mapper.map(crlUrlDTO, CrlUrl.class)));
        switch (schemeDTO.getOrder()) {
            case END:
                newScheme.setSort(schemeRepository.findMaxSort() + 1);
                break;
            case BEGIN:
                List<Scheme> schemes = schemeRepository.findAll(Sort.by("sort"));
                for (int i = 0; i < schemes.size(); i++) {
                    Scheme dbScheme = schemes.get(i);
                    if (i == 0) {
                        newScheme.setSort(dbScheme.getSort());
                    }
                    dbScheme.setSort(dbScheme.getSort() + 1);
                    schemeRepository.save(dbScheme);
                }
                break;
        }
        return schemeToDTO(schemeRepository.save(newScheme));
    }

    public SchemeDTO save(SchemeDTO schemeDTO) {
        Scheme scheme = schemeRepository.findById(schemeDTO.getId()).orElseThrow(() -> new RuntimeException("Scheme record not found"));
        scheme.removeCrlUrls();
        mapper.map(schemeDTO, scheme);
        schemeDTO.getCrlUrls().forEach(crlUrlDTO -> scheme.addCrlUrl(mapper.map(crlUrlDTO, CrlUrl.class)));
        updateNotificationGroups(schemeDTO, scheme);
        return schemeToDTO(schemeRepository.save(scheme));
    }

    private void updateNotificationGroups(SchemeDTO schemeDTO, Scheme scheme) {
        scheme.removeNotificationGroups();
        for (Long id : schemeDTO.getNotificationGroupIds()) {
            scheme.addNotificationGroup(notificationGroupRepository.getOne(id));
        }
    }

    public void deleteById(Long id) {
        Long sort = schemeRepository.getOne(id).getSort();
        List<Scheme> schemes = schemeRepository.findBySortGreaterThan(sort, Sort.by("sort"));
        for (Scheme scheme : schemes) {
            scheme.setSort(sort);
            schemeRepository.save(scheme);
            sort++;
        }

        schemeRepository.deleteById(id);
    }

    public SchemeDTO findScheme(Long id) {
        final Scheme scheme = schemeRepository.findOneWithUrlsById(id).orElseThrow(() -> new RuntimeException("Scheme record not found"));
        return schemeToDTO(schemeRepository.save(scheme));
    }

    public List<SchemeDTO> moveUpDown(Long id, DIRECTION direction) {
        Scheme scheme = schemeRepository.getOne(id);
        Long sort = scheme.getSort();
        Scheme dbScheme = null;
        switch (direction) {
            case UP:
                dbScheme = schemeRepository.findTopBySortLessThan(sort, Sort.by("sort").descending());
                break;
            case DOWN:
                dbScheme = schemeRepository.findTopBySortGreaterThan(sort, Sort.by("sort").ascending());
                break;
        }
        if (dbScheme != null) {
            scheme.setSort(dbScheme.getSort());
            schemeRepository.save(scheme);
            dbScheme.setSort(sort);
            schemeRepository.save(dbScheme);
        }

        return findAll();
    }

    private SchemeDTO schemeToDTO(Scheme scheme) {
        final SchemeDTO schemeDTO = mapper.map(scheme, SchemeDTO.class);
        scheme.getNotificationGroups().forEach(notificationGroup -> schemeDTO.getNotificationGroupIds().add(notificationGroup.getId()));
        return schemeDTO;
    }

    public Set<NotificationGroup> findSchemeNotificationGroups(Long id) {
        Scheme scheme = schemeRepository.findOneWithNotificationGroupsById(id);
        return scheme.getNotificationGroups();
    }

    public enum DIRECTION {
        UP,
        DOWN
    }
}
