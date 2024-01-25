package com.javarush.jira.ref;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefUtil {
    @Autowired
    private final ReferenceService referenceService;

    public void updateRef(RefType refType) {
        referenceService.updateRefs(refType);
    }
}
