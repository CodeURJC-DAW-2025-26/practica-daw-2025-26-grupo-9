import { useEffect, useRef } from "react";
import type { Route } from "./+types/stats";
import { getTopPosts } from "~/services/posts.service";
import Sidebar from "~/components/sidebar";
import { requireAuth } from "~/utils/authGuard";

declare global {
  interface Window {
    Chart: any;
  }
}

export async function clientLoader() {
  return requireAuth(async () => {
    const topPosts = await getTopPosts();
    return { topPosts };
  });
}

export function meta() {
  return [
    { title: "eQuis - Estadísticas" },
  ];
}

export default function Stats({ loaderData }: Route.ComponentProps) {
  const { topPosts } = loaderData;
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const chartRef = useRef<any>(null);

  useEffect(() => {
    const initChart = () => {
      if (!canvasRef.current || typeof window.Chart === "undefined") return;
      if (chartRef.current) {
        chartRef.current.destroy();
      }

      const labels = topPosts.map(p =>
        p.content.length > 30 ? p.content.substring(0, 30) + "..." : p.content
      );
      const data = topPosts.map(p => p.likesCount);

      chartRef.current = new window.Chart(canvasRef.current, {
        type: "bar",
        data: {
          labels,
          datasets: [{
            label: "Número de Likes",
            data,
            borderWidth: 1,
          }],
        },
        options: {
          responsive: true,
          scales: {
            y: { beginAtZero: true },
          },
        },
      });
    };

    if (typeof window.Chart === "undefined") {
      const script = document.createElement("script");
      script.src = "https://cdn.jsdelivr.net/npm/chart.js";
      script.onload = initChart;
      document.body.appendChild(script);
    } else {
      initChart();
    }

    return () => {
      if (chartRef.current) {
        chartRef.current.destroy();
        chartRef.current = null;
      }
    };
  }, [topPosts]);

  return (
    <>
      <Sidebar />
      <div className="col-md-10 mt-4">
        <div className="card shadow-sm">
          <div className="card-body">
            <h4 className="mb-4">Posts con m&aacute;s Likes</h4>
            <canvas id="likesChart" ref={canvasRef}></canvas>
            {topPosts.length === 0 && (
              <p className="text-muted text-center mt-3">No hay datos disponibles.</p>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
