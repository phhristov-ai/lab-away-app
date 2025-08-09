import { RefObject, useEffect, useRef, useState } from 'react';

const useArrowAnimation = (
  sectionRefs: RefObject<HTMLDivElement | null>[] ,
  maxHeight: number,
  animationDuration: number
) => {
  const [arrowHeights, setArrowHeights] = useState(new Array(sectionRefs.length).fill(0));
  const arrowHeightsRef = useRef(arrowHeights);
  const animationRefs = useRef<(number | null)[]>(new Array(sectionRefs.length).fill(null));
  const animatingRef = useRef(new Array(sectionRefs.length).fill(false));

  useEffect(() => {
    arrowHeightsRef.current = arrowHeights;
  }, [arrowHeights]);

  useEffect(() => {
    const animateArrow = (index: number, from: number, to: number, startTime: number) => {
      animatingRef.current[index] = true;

      const animate = (time: number) => {
        const elapsed = time - startTime;
        const progress = Math.min(elapsed / animationDuration, 1);
        const currentHeight = from + (to - from) * progress;

        setArrowHeights((prev) => {
          const newHeights = [...prev];
          newHeights[index] = currentHeight;
          return newHeights;
        });

        if (progress < 1) {
          animationRefs.current[index] = requestAnimationFrame(animate);
        } else {
          animatingRef.current[index] = false;
          animationRefs.current[index] = null;
        }
      };

      if (animationRefs.current[index]) {
        cancelAnimationFrame(animationRefs.current[index]!);
      }
      animationRefs.current[index] = requestAnimationFrame(animate);
    };

    const onScroll = () => {
      const windowHeight = window.innerHeight;
      const UP_TRIGGER = windowHeight * 0.60;
      const DOWN_TRIGGER = windowHeight * 0.60;

      sectionRefs.forEach((ref, index) => {
        if (!ref.current) return;

        const rect = ref.current.getBoundingClientRect();
        const currentHeight = arrowHeightsRef.current[index];

        if (
          rect.top < UP_TRIGGER &&
          currentHeight < maxHeight &&
          !animatingRef.current[index]
        ) {
          animateArrow(index, currentHeight, maxHeight, performance.now());
        } else if (
          rect.top > DOWN_TRIGGER &&
          currentHeight > 0 &&
          !animatingRef.current[index]
        ) {
          animateArrow(index, currentHeight, 0, performance.now());
        }
      });
    };

    window.addEventListener('scroll', onScroll);
    onScroll();

    return () => {
      window.removeEventListener('scroll', onScroll);
      animationRefs.current.forEach((frameId) => {
        if (frameId !== null) cancelAnimationFrame(frameId);
      });
    };
  }, [sectionRefs, maxHeight, animationDuration]);

  return arrowHeights;
};

export default useArrowAnimation;
