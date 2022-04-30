package oslomet.inputvalidering;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class MotorvognController {

    @Autowired
    private MotorvognRepository rep;

    private Logger logger = LoggerFactory.getLogger(MotorvognController.class);

    @GetMapping("/hentBiler")
    public List<Bil> hentBiler(HttpServletResponse response) throws IOException {
        List<Bil> alleBiler = rep.hentAlleBiler();
        if(alleBiler==null){
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Feil i DB -prøv igjen senere");
        }
        return alleBiler;
    }

    //lager inputvalidering
    @PostMapping("/lagre")
    public void lagreKunde(Motorvogn motorvogn, HttpServletResponse response) throws IOException {
        if (validerMotorvognOk(motorvogn)) {
            if (!rep.lagreMotorvogn(motorvogn)) {
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Feil i DB -prøv igjen senere");
            }
        }else {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Valideringsfeil -prøv igjen senere");
        }
    }

    @GetMapping("/hentAlle")
    public List<Motorvogn> hentAlleMotorvogner(HttpServletResponse response) throws IOException {
        List<Motorvogn> alleMotorvogner = rep.hentAlleMotorvogner();
        if(alleMotorvogner==null){
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Feil i DB -prøv igjen senere");
        }
        return alleMotorvogner;
    }

    @GetMapping("/henteEnMotorvogn")
    public Motorvogn henteEnMotorvogn(int id, HttpServletResponse response) throws IOException{
        Motorvogn enMotorvogn = rep.henteEnMotorvogn(id);
        if(enMotorvogn == null){
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Feil i DB -prøv igjen senere");
        }
        return enMotorvogn;
    }

    //lager inputvalidering
    @PostMapping("/endre")
    public void endre(Motorvogn motorvogn, HttpServletResponse response) throws IOException {
        if (validerMotorvognOk(motorvogn)){
            if (!rep.endreMotorvogn(motorvogn)) {
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Feil i DB -prøv igjen senere");
            }
         }else {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Valideringsfeil -prøv igjen senere");
        }
    }

    @GetMapping("/slettEnMotorvogn")
    public void slettEnMotorvogn(String personnr, HttpServletResponse response) throws IOException{
        if(!rep.slettEnMotorvogn(personnr)){
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Feil i DB -prøv igjen senere");
        }
    }

    @GetMapping("/slettAlle")
    public void slettAlleMotorvogner(HttpServletResponse response) throws IOException{
        if(!rep.slettAlleMotorvogner()){
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Feil i DB -prøv igjen senere");
        }
    }

    private boolean validerMotorvognOk(Motorvogn motorvogn){
        String regexPersonnr = "[0-9]{11}";
        String regexNavn = "[a-zA-ZæøåÆØÅ. \\-]{2,20}";
        String regexAdresse = "[0-9a-zA-ZæøåÆØÅ. \\-]{2,30}";
        String regexKjennetegn = "[A-Z][A-Z][0-9]{5}";
        String regexMerke = "[0-9a-zA-ZøæåØÆÅ. \\-]";

        boolean personnrOk = motorvogn.getPersonnr().matches(regexPersonnr);
        boolean navnOk = motorvogn.getNavn().matches(regexNavn);
        boolean adresseOk = motorvogn.getAdresse().matches(regexAdresse);
        boolean kjennetegnOk = motorvogn.getKjennetegn().matches(regexKjennetegn);
        boolean merkeOk = motorvogn.getMerke().matches(regexMerke);

        if (personnrOk && navnOk && adresseOk && kjennetegnOk && merkeOk){
            return true;
        }
        logger.error("valideringsfeil");
        return false;
    }
}
