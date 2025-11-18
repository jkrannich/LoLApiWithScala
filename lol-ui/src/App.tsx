import { useState } from "react";

type MatchSummary = {
    matchId: string;
    gameMode: string;
    durationSeconds: number;
    champion: string;
    kills: number;
    deaths: number;
    assists: number;
    win: boolean;
};

function App() {
     const [name, setName] = useState("Thayger");
      const [tag, setTag] = useState("Soul");
      const [region, setRegion] = useState("EUROPE");
      const [matches, setMatches] = useState<MatchSummary[]>([]);
      const [loading, setLoading] = useState(false);
      const [error, setError] = useState<string | null>(null);

      const loadMatches = async () => {
        setLoading(true);
        setError(null);
        setMatches([]);

        try {
          const params = new URLSearchParams({
            name,
            tag,
            region,
            limit: "5",
          });

          const res = await fetch(
            `http://localhost:8080/matchhistory?${params.toString()}`
          );

          if (!res.ok) {
            const text = await res.text();
            throw new Error(text || `Request failed with ${res.status}`);
          }

          const data: MatchSummary[] = await res.json();
          setMatches(data);
        } catch (e: any) {
          setError(e.message ?? "Unknown error");
        } finally {
          setLoading(false);
        }
      };

      return (
        <div className="app">
          <h1 className="app-title">LoL Match History</h1>

          <div className="controls">
            <label className="field">
              <span>Name:</span>
              <input value={name} onChange={(e) => setName(e.target.value)} className="input"/>
            </label>

            <label className="field">
              <span>Tag:</span>
              <input value={tag} onChange={(e) => setTag(e.target.value)} className="input"/>
            </label>

            <label className="field">
              <span>Region:</span>
              <select value={region} onChange={(e) => setRegion(e.target.value)} className="select">
                <option value="EUROPE">EUROPE</option>
                <option value="AMERICAS">AMERICAS</option>
                <option value="ASIA">ASIA</option>
              </select>
            </label>

            <button onClick={loadMatches} disabled={loading} className="button button-primary">
              {loading ? "Loading..." : "Load Match History"}
            </button>
          </div>

          {error &&
            <div className="error">
              Error: {error}
            </div>
          }

          {matches.length > 0 && (
            <table className="matches-table">
              <thead>
                <tr>
                  <th style={{ borderBottom: "1px solid #ccc", textAlign: "left" }}>Champion</th>
                  <th style={{ borderBottom: "1px solid #ccc", textAlign: "left" }}>Mode</th>
                  <th style={{ borderBottom: "1px solid #ccc", textAlign: "left" }}>K / D / A</th>
                  <th style={{ borderBottom: "1px solid #ccc", textAlign: "left" }}>Win</th>
                  <th style={{ borderBottom: "1px solid #ccc", textAlign: "left" }}>Duration</th>
                </tr>
              </thead>
              <tbody>
                {matches.map((m) => (
                  <tr key={m.matchId}>
                    <td>{m.champion}</td>
                    <td>{m.gameMode}</td>
                    <td>
                      {m.kills} / {m.deaths} / {m.assists}
                    </td>
                    <td className={m.win ? "win" : "loss"}>
                      {m.win ? "Win" : "Loss"}
                    </td>
                    <td>{Math.round(m.durationSeconds / 60)} min</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          {!loading && !error && matches.length === 0 && (
            <p className="empty-message">No matches loaded yet. Try searching!</p>
          )}
        </div>
      );
    }

export default App;