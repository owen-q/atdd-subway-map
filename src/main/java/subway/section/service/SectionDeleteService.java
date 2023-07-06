package subway.section.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import subway.line.domain.Line;
import subway.line.service.LineService;
import subway.section.domain.Section;
import subway.section.exception.InvalidSectionDeleteException;
import subway.section.repository.SectionRepository;
import subway.station.domain.Station;
import subway.station.service.StationService;
import subway.support.ErrorCode;

@Service
@Slf4j
@RequiredArgsConstructor
public class SectionDeleteService {
    private final LineService lineService;
    private final StationService stationService;
    private final SectionRepository sectionRepository;

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Line line = lineService.getLine(lineId);
        Station station = stationService.get(stationId);

        if (line.isLastStation(station)) {
            throw new InvalidSectionDeleteException(ErrorCode.SECTION_DELETE_FAIL_BY_LAST_STATION_REMOVED);
        }

        Optional<Section> maybeSection = sectionRepository.findByLineAndDownStation(line, station);

        if (maybeSection.isEmpty()) {
            throw new InvalidSectionDeleteException(ErrorCode.SECTION_DELETE_FAIL_BY_NOT_ALLOWED_STATION);
        }

        sectionRepository.delete(maybeSection.get());
    }
}
