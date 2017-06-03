package fr.ensimag.loupsgarous.modele;

import java.util.HashMap;

/**
 *
 * @author baplar
 */
public class ProbaPouvoir {
    private int idPartie = -1;
    private Pouvoir pouvoir;
    private float proba;

    public ProbaPouvoir(int idPartie, Pouvoir pouvoir, float proba) {
        this.idPartie = idPartie;
        this.pouvoir = pouvoir;
        this.proba = proba;
    }

    public ProbaPouvoir(Pouvoir pouvoir, float proba) {
        this.pouvoir = pouvoir;
        this.proba = proba;
    }

    public int getIdPartie() {
        return idPartie;
    }

    public Pouvoir getPouvoir() {
        return pouvoir;
    }

    public float getProba() {
        return proba;
    }

    public static HashMap<Pouvoir, ProbaPouvoir> initProbas(float contamination, float insomnie, float voyance, float spiritisme) {
        HashMap<Pouvoir, ProbaPouvoir> probasPouvoirs = new HashMap<>();
        probasPouvoirs.put(Pouvoir.CONTAMINATION, new ProbaPouvoir(Pouvoir.CONTAMINATION, contamination));
        probasPouvoirs.put(Pouvoir.INSOMNIE, new ProbaPouvoir(Pouvoir.INSOMNIE, insomnie));
        probasPouvoirs.put(Pouvoir.VOYANCE, new ProbaPouvoir(Pouvoir.VOYANCE, voyance));
        probasPouvoirs.put(Pouvoir.SPIRITISME, new ProbaPouvoir(Pouvoir.SPIRITISME, spiritisme));
        return probasPouvoirs;
    }

}
