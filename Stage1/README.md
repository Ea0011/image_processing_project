# The succession of Gaussian Blur (sigma = 3) -> Medain Filter (sigma = 10) before applying layer filters Helps in making results better

The idea behind applying this filter before applying layer filters is to make colour features of face area prominent. Using Gaussian Filter with small radius (3) helps getting rid of available noise in the pictures. Median filter with bigger radius helps to modify pixels of similar face area so that they become close to one another in terms of colour. For instance, it makes pixels in lip area eminate similar colour or make area around the noise eminate similar color. This means that features of face become prominent whihc helps algorithms to easilty detect those.

# After the application of layer filters, we apply Minimum Filter (sigma = 1) to remove unnecessary noise in binary images

# An optimized kernel would be

| 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   |
| 1   | 1   | 1   | 1   | 1   | 3   | 1   | 1   | 1   | 1   | 1   |
| 1   | 1   | 1   | 1   | 1   | 5   | 1   | 1   | 1   | 1   | 1   |
| 1   | 1   | 1   | 1   | 1   | 10  | 1   | 1   | 1   | 1   | 1   |
| 1   | 1   | 3   | 5   | 10  | 20  | 10  | 5   | 3   | 1   | 1   |
| 1   | 1   | 1   | 1   | 1   | 10  | 1   | 1   | 1   | 1   | 1   |
| 1   | 1   | 1   | 1   | 1   | 5   | 1   | 1   | 1   | 1   | 1   |
| 1   | 1   | 1   | 1   | 1   | 3   | 1   | 1   | 1   | 1   | 1   |
| 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   |
| 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   | 1   |


This filter tries to approximate the application of the succession of the above mentioned filters. It gives more weight to the center pixel and less weight to pixels with more radius from the center. This gives an effect of blurring the image and fixing small distortions in the image. The effect is similar to the effect of above mentioned strategy.

# Stage 1.3 (Observations regarding Binary Layer 3)

The smile causes a gap to appear between the area where Layer 3 would normally detect lips. Also, the nasal are shift upwards as the smile causes the are around the mouth to enlarge. The rotation of head does not affect what features it detects. However, we can see that it detects only the features that are facing more towards the camera. For instance, when the head is fully rotated, full ear is detected and some part of lips can be detected.
