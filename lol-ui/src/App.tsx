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
        <div style={{ maxWidth: 800, margin: "2rem auto", fontFamily: "sans-serif" }}>
          <h1>LoL Match History</h1>

          <div style={{ marginBottom: "1rem" }}>
            <label>
              Name:{" "}
              <input value={name} onChange={(e) => setName(e.target.value)} />
            </label>{" "}
            <label>
              Tag:{" "}
              <input value={tag} onChange={(e) => setTag(e.target.value)} />
            </label>{" "}
            <label>
              Region:{" "}
              <select value={region} onChange={(e) => setRegion(e.target.value)}>
                <option value="EUROPE">EUROPE</option>
                <option value="AMERICAS">AMERICAS</option>
                <option value="ASIA">ASIA</option>
              </select>
            </label>{" "}
            <button onClick={loadMatches} disabled={loading}>
              {loading ? "Loading..." : "Load Match History"}
            </button>
          </div>

          {error && (
            <div style={{ color: "red", marginBottom: "1rem" }}>
              Error: {error}
            </div>
          )}

          {matches.length > 0 && (
            <table
              style={{
                width: "100%",
                borderCollapse: "collapse",
                marginTop: "1rem",
              }}
            >
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
                    <td style={{ color: m.win ? "green" : "red" }}>
                      {m.win ? "Win" : "Loss"}
                    </td>
                    <td>{Math.round(m.durationSeconds / 60)} min</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          {!loading && !error && matches.length === 0 && (
            <p>No matches loaded yet. Try searching!</p>
          )}
        </div>
      );
    }

export default App;