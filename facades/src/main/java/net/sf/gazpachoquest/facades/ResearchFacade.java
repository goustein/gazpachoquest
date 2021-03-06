/*******************************************************************************
 * Copyright (c) 2014 antoniomariasanchez at gmail.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     antoniomaria - initial API and implementation
 ******************************************************************************/
package net.sf.gazpachoquest.facades;

import net.sf.gazpachoquest.dto.ResearchDTO;
import net.sf.gazpachoquest.dto.UserDTO;
import net.sf.gazpachoquest.dto.support.PageDTO;
import net.sf.gazpachoquest.types.EntityStatus;

public interface ResearchFacade {

    void delete(Integer id);

    ResearchDTO findOne(Integer id);

    ResearchDTO save(ResearchDTO research);

    PageDTO<ResearchDTO> findPaginated(Integer pageNumber, Integer size);

    void addRespondent(Integer researchId, UserDTO respondentDTO);

    void changeStatus(Integer researchId, EntityStatus newStatus);

}
