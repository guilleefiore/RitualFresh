import { useId } from 'react';

const VIEWBOX_WIDTH = 680;
const VIEWBOX_HEIGHT = 260;
const CHART_LEFT = 56;
const CHART_TOP = 28;
const CHART_WIDTH = 580;
const CHART_HEIGHT = 164;

export function TimeSeriesChart({ title, description, buckets, valueKey, formatValue, tone = 'primary' }) {
  const titleId = useId();
  const descriptionId = useId();
  const values = buckets.map((bucket) => Number(bucket[valueKey] || 0));
  const maxValue = Math.max(...values, 1);
  const points = values.map((value, index) => {
    const x = values.length === 1
      ? CHART_LEFT + CHART_WIDTH / 2
      : CHART_LEFT + (index * CHART_WIDTH) / (values.length - 1);
    const y = CHART_TOP + CHART_HEIGHT - (value / maxValue) * CHART_HEIGHT;
    return { x, y, value, bucket: buckets[index] };
  });
  const visibleLabelIndexes = new Set([0, Math.floor((points.length - 1) / 2), points.length - 1]);

  return (
    <figure className={`history-chart history-chart--${tone}`}>
      <figcaption>
        <h2>{title}</h2>
        <p>{description}</p>
      </figcaption>
      <svg
        viewBox={`0 0 ${VIEWBOX_WIDTH} ${VIEWBOX_HEIGHT}`}
        role="img"
        aria-labelledby={`${titleId} ${descriptionId}`}
      >
        <title id={titleId}>{title}</title>
        <desc id={descriptionId}>{description}</desc>
        {[0, 0.5, 1].map((ratio) => {
          const y = CHART_TOP + CHART_HEIGHT * ratio;
          return <line key={ratio} className="history-chart__grid" x1={CHART_LEFT} y1={y} x2={CHART_LEFT + CHART_WIDTH} y2={y} />;
        })}
        <polyline
          className="history-chart__line"
          points={points.map(({ x, y }) => `${x},${y}`).join(' ')}
        />
        {points.map(({ x, y, value, bucket }, index) => (
          <g key={`${bucket.from}-${bucket.to}`}>
            <circle className="history-chart__point" cx={x} cy={y} r="6">
              <title>{`${formatBucketLabel(bucket)}: ${formatValue(value)}`}</title>
            </circle>
            {visibleLabelIndexes.has(index) ? (
              <text className="history-chart__label" x={x} y={CHART_TOP + CHART_HEIGHT + 32} textAnchor="middle">
                {formatShortDate(bucket.from)}
              </text>
            ) : null}
          </g>
        ))}
      </svg>
      <ul className="sr-only">
        {points.map(({ value, bucket }) => (
          <li key={`${bucket.from}-${bucket.to}-accessible`}>
            {formatBucketLabel(bucket)}: {formatValue(value)}
          </li>
        ))}
      </ul>
    </figure>
  );
}

function formatBucketLabel(bucket) {
  if (bucket.from === bucket.to) return formatLongDate(bucket.from);
  return `Del ${formatLongDate(bucket.from)} al ${formatLongDate(bucket.to)}`;
}

function formatShortDate(value) {
  return new Intl.DateTimeFormat('es-AR', { day: '2-digit', month: 'short', timeZone: 'UTC' })
    .format(new Date(`${value}T00:00:00Z`));
}

function formatLongDate(value) {
  return new Intl.DateTimeFormat('es-AR', { dateStyle: 'medium', timeZone: 'UTC' })
    .format(new Date(`${value}T00:00:00Z`));
}
