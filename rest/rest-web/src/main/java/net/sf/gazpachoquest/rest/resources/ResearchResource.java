package net.sf.gazpachoquest.rest.resources;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.gazpachoquest.domain.user.User;
import net.sf.gazpachoquest.dto.ResearchDTO;
import net.sf.gazpachoquest.dto.UserDTO;
import net.sf.gazpachoquest.dto.support.PageDTO;
import net.sf.gazpachoquest.facades.ResearchFacade;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Api(value = "/researches", description = "Researches Interface")
@Produces(MediaType.APPLICATION_JSON)
@Path("/researches")
public class ResearchResource {

    private static final Logger logger = LoggerFactory.getLogger(ResearchResource.class);

    @Autowired
    private ResearchFacade researchFacade;

    public ResearchResource() {
        super();
    }

    @GET
    @Path("/")
    @ApiOperation(value = "Get research list", notes = "More notes about this method", response = PageDTO.class)
    @ApiResponses(value = { @ApiResponse(code = 404, message = "Invalid invitation token supplied"),
            @ApiResponse(code = 200, message = "Researches available") })
    public PageDTO<ResearchDTO> getResearches(
            @NotNull @ApiParam(name = "pageNumber", value = "Refers page number", required = true) @QueryParam("pageNumber") Integer pageNumber,
            @ApiParam(name = "size", value = "Refers page size", required = true) @NotNull @QueryParam("size") Integer size) {
        User principal = (User) SecurityUtils.getSubject().getPrincipal();
        logger.debug("{} fetching existing researches", principal.getFullName());

        return researchFacade.findPaginated(pageNumber, size);
    }

    @POST
    @Path("/")
    @ApiOperation(value = "Save research")
    @ApiResponses(value = { @ApiResponse(code = 404, message = "Invalid invitation token supplied"),
            @ApiResponse(code = 200, message = "Successfully saved") })
    @Consumes(MediaType.APPLICATION_JSON)
    public ResearchDTO saveResearch(@ApiParam(value = "Research", required = true) ResearchDTO researchDTO) {
        User principal = (User) SecurityUtils.getSubject().getPrincipal();
        logger.debug("{} starting a new research", principal.getFullName());
        return researchFacade.save(researchDTO);
    }

    @POST
    @Path("/{researchId}/addRespondent")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Add the respondent to existing research")
    @ApiResponses(value = { @ApiResponse(code = 404, message = "Invalid invitation token supplied"),
            @ApiResponse(code = 200, message = "Respondent added correctly") })
    public Response saveAnswer(@ApiParam(value = "Respondent", required = true) UserDTO respondentDTO,
            @NotNull @PathParam("researchId") @ApiParam(value = "Research id", required = true) Integer researchId) {
        Subject subject = SecurityUtils.getSubject();
        User principal = (User) SecurityUtils.getSubject().getPrincipal();
        subject.checkPermission("research:update:" + researchId);

        logger.debug("User {} adding respondent to researchId = {}", principal.getFullName(), researchId);
        researchFacade.addRespondent(researchId, respondentDTO);
        return Response.ok().build();
    }
}
