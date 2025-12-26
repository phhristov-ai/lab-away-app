import { RefObject, useEffect, useRef, useState } from "react";

const TRIGGER_RATIO = 0.6;

function useArrowAnimation<T extends HTMLElement>(
  sectionRefs: RefObject<T | null>[],
  maxHeight: number,
  animationDuration: number
) {
  const [arrowHeights, setArrowHeights] = useState(
    () => new Array(sectionRefs.length).fill(0)
  );

  const arrowHeightsRef = useRef(arrowHeights);
  const animationRefs = useRef<(number | null)[]>(
    new Array(sectionRefs.length).fill(null)
  );
  const animatingRef = useRef<boolean[]>(
    new Array(sectionRefs.length).fill(false)
  );

  useEffect(() => {
    arrowHeightsRef.current = arrowHeights;
  }, [arrowHeights]);

  function animateArrow(
    index: number,
    from: number,
    to: number,
    startTime: number
  ) {
    animatingRef.current[index] = true;

    const step = (time: number) => {
      const elapsed = time - startTime;
      const progress = Math.min(elapsed / animationDuration, 1);
      const height = from + (to - from) * progress;

      setArrowHeights((prev) => {
        const next = [...prev];
        next[index] = height;
        return next;
      });

      if (progress < 1) {
        animationRefs.current[index] = requestAnimationFrame(step);
      } else {
        animatingRef.current[index] = false;
        animationRefs.current[index] = null;
      }
    };

    if (animationRefs.current[index] !== null) {
      cancelAnimationFrame(animationRefs.current[index]);
    }

    animationRefs.current[index] = requestAnimationFrame(step);
  }

  function handleScroll() {
    const trigger = window.innerHeight * TRIGGER_RATIO;

    sectionRefs.forEach((ref, index) => {
      if (!ref.current || animatingRef.current[index]) return;

      const top = ref.current.getBoundingClientRect().top;
      const height = arrowHeightsRef.current[index];

      if (top < trigger && height < maxHeight) {
        animateArrow(index, height, maxHeight, performance.now());
        return;
      }

      if (top > trigger && height > 0) {
        animateArrow(index, height, 0, performance.now());
      }
    });
  }

  useEffect(() => {
    window.addEventListener("scroll", handleScroll);
    handleScroll();

    return () => {
      window.removeEventListener("scroll", handleScroll);
      animationRefs.current.forEach((id) => {
        if (id !== null) cancelAnimationFrame(id);
      });
    };
  }, [sectionRefs, maxHeight, animationDuration]);

  return arrowHeights;
}

export default useArrowAnimation;
